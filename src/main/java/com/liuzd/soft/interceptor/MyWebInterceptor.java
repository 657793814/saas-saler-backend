package com.liuzd.soft.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.liuzd.soft.config.DynamicDataSource;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.impl.LoginServiceImpl;
import com.liuzd.soft.utils.SignUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;


/**
 * @description web拦截器，拦截请求设置动态数据源
 * @author:liuzd01
 * @date:2023/10/18
 * @email:liuzd2025@qq.com
 **/
@Slf4j
@Data
public class MyWebInterceptor implements HandlerInterceptor {

    private static final Set<String> DEFAULT_NO_TENANT_CODE_URIS = ImmutableSet.of(
            "/error",
            "/favicon.ico",
            "/swagger-ui.html",
            "/csrf"
    );

    // 无租户信息的接口前缀
    private static final Set<String> DEFAULT_NO_TENANT_CODE_URI_PREFIXES = ImmutableSet.of(
            "/swagger",
            "/actuator",
            "/test"
    );

    // 无需token的接口前缀
    private static final Set<String> NO_TOKEN_URI_PREFIXES = ImmutableSet.of(
            "/login",
            "/refresh_token",
            "/actuator",
            "/minio"
    );

    private DynamicDataSource dynamicDataSource;
    private LoginServiceImpl loginServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //check and set tenantCode
        checkAndSetTenantCode(request);

        //设置TraceId
        setTraceId(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除MDC中的traceId，防止内存泄漏
        MDC.remove(GlobalConstant.LOGTRACE_TRACEID);
        // 清除ThreadContextHolder中的内容
        ThreadContextHolder.clear();
    }

    public void checkAndSetTenantCode(HttpServletRequest request) throws IOException {
        String uri = request.getRequestURI();
        // 内置的无租户code接口名单
        if (DEFAULT_NO_TENANT_CODE_URIS.contains(uri)) {
            return;
        }
        if (matchUriPrefix(uri, DEFAULT_NO_TENANT_CODE_URI_PREFIXES)) {
            return;
        }

        //set tenantCode and dataSource
        setCurrentDataSource(request);

        //验证token
        checkToken(request);
    }

    /**
     * 获取请求体内容
     * 在Servlet API中，请求体是一个 InputStream 时只能被读取一次。当拦截器读取了请求体后接口将无法获取
     * 此处通过 MultiReadHttpServletRequest 包装一下，可以多次获取
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getBody(HttpServletRequest request) throws IOException {
        String body = "";
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            if (request instanceof ContentCachingFilter.MultiReadHttpServletRequest) {
                body = ((ContentCachingFilter.MultiReadHttpServletRequest) request).getBody();
            } else {
                body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            }
        }
        return body;
    }

    private void checkToken(HttpServletRequest request) throws IOException {
        if (matchUriPrefix(request.getRequestURI(), NO_TOKEN_URI_PREFIXES)) {
            return;
        }

        String body = getBody(request);
        log.debug("Request body: {}", body);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);

        String token = jsonNode.has(GlobalConstant.REQUEST_PARAM_TOKEN_KEY) ?
                jsonNode.get(GlobalConstant.REQUEST_PARAM_TOKEN_KEY).asText() : null;
        long timestamp = jsonNode.has(GlobalConstant.REQUEST_PARAM_TIMESTAMP_KEY) ?
                jsonNode.get(GlobalConstant.REQUEST_PARAM_TIMESTAMP_KEY).asLong() : 0L;
        String randStr = jsonNode.has(GlobalConstant.REQUEST_PARAM_RAND_STR_KEY) ?
                jsonNode.get(GlobalConstant.REQUEST_PARAM_RAND_STR_KEY).asText() : null;
        String sign = jsonNode.has(GlobalConstant.REQUEST_PARAM_SIGN_KEY) ?
                jsonNode.get(GlobalConstant.REQUEST_PARAM_SIGN_KEY).asText() : null;

        //10分钟实效性，防抓链接攻击
        if (timestamp - (System.currentTimeMillis() / 1000) > 600) {
            throw MyException.exception(RetEnums.VERIFY_SIGN_TIMESTAMP_FAIL);
        }

        //校验sign
        if (!SignUtils.verify(randStr, timestamp, sign, token)) {
            throw MyException.exception(RetEnums.VERIFY_SIGN_FAIL);
        }

        //通过token 获取登录信息
        TokenInfo tokenInfo = loginServiceImpl.getTokenInfo(token);
        if (ObjectUtils.isEmpty(tokenInfo)) {
            throw MyException.exception(RetEnums.LOGIN_EXPIRE);
        }

        if (!ThreadContextHolder.getTenantCode().equals(tokenInfo.getTenantCode())) {
            throw MyException.exception(RetEnums.LOGIN_EXPIRE, "租户信息校验失败");
        }

        //校验成功 将tokenInfo 设置到线程上下文中
        ThreadContextHolder.put(GlobalConstant.LOGIN_USER_INFO, tokenInfo);

    }

    /**
     * 设置当前数据源
     *
     * @param request
     */
    private void setCurrentDataSource(HttpServletRequest request) {
        if (ObjectUtil.isNotEmpty(ThreadContextHolder.getTenantCode())) {
            dynamicDataSource.switchTenant(ThreadContextHolder.getTenantCode());
            return;
        }

        //判断请求头是否有特性的tenantCode
        String tenantCode = request.getHeader(GlobalConstant.HEADER_TENANT_CODE);
        if (StringUtil.isNotBlank(tenantCode)) {
            dynamicDataSource.switchTenant(tenantCode);
            ThreadContextHolder.putTenantCode(tenantCode);
            return;
        }

        //判断请求参数是否有tenantCode
        tenantCode = request.getParameter(GlobalConstant.REQUEST_PARAM_TENANT_CODE_KEY);
        if (StringUtil.isNotBlank(tenantCode)) {
            dynamicDataSource.switchTenant(tenantCode);
            ThreadContextHolder.putTenantCode(tenantCode);
            return;
        }

        //判断请求参数是否有tenantId
        String tenantId = request.getParameter(GlobalConstant.REQUEST_PARAM_TENANT_ID_KEY);
        if (StringUtil.isNotBlank(tenantId)) {
            tenantCode = dynamicDataSource.switchTenantByTenantId(tenantId);
            ThreadContextHolder.putTenantCode(tenantCode);
            return;
        }

        //没有tenantCode抛出异常
        throw MyException.exception(RetEnums.UNKNOWN_TENANT_REQUEST);

    }

    /**
     * 设置日志链路的traceId
     */
    private void setTraceId(HttpServletRequest request) {
        //判断请求头是否由traceId
        String traceId = request.getHeader(GlobalConstant.LOGTRACE_FAST_TRACEID);
        if (!StringUtils.hasText(traceId)) {
            traceId = generateTrackId();
        }

        ThreadContextHolder.put(GlobalConstant.LOGTRACE_FAST_TRACEID, traceId);
        ThreadContextHolder.put(GlobalConstant.LOGTRACE_TRACEID, traceId);

        // 将traceId放入MDC中，以便在日志中输出
        MDC.put(GlobalConstant.LOGTRACE_TRACEID, traceId);
        log.debug("Setting traceId in MDC: {}", traceId);
    }

    /**
     * 生成traceId
     *
     * @return
     */
    public static String generateTrackId() {
        return UUID.randomUUID().toString().replace("-", "") +
                "." + Thread.currentThread().getId() + "." + System.currentTimeMillis();
    }


    /**
     * 判断uri是否匹配
     *
     * @param uri
     * @param prefixes
     * @return
     */
    private boolean matchUriPrefix(String uri, Collection<String> prefixes) {
        if (!StringUtils.hasText(uri) || CollectionUtils.isEmpty(prefixes)) {
            return false;
        }
        for (String prefix : prefixes) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
