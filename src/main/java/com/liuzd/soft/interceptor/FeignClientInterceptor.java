package com.liuzd.soft.interceptor;

import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * FeignClient拦截器
 *
 * @author liuzd
 * @date 2021/7/27 14:03
 */
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //设置分布式追踪traceId
        template.header(GlobalConstant.HEADER_TENANT_CODE, ThreadContextHolder.getTenantCode());
        template.header(GlobalConstant.LOGTRACE_FAST_TRACEID, GlobalConstant.LOGTRACE_TRACEID);
        log.info("header:{}", template.headers());

    }

}
