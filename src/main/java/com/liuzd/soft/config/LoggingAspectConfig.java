package com.liuzd.soft.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 自定义日志切面
 *
 * @author: liuzd
 * @date: 2025/8/20
 * @email: liuzd2025@qq.com
 * @desc
 */
@Slf4j
@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfig {
    @Pointcut("@annotation(com.liuzd.soft.annotation.LogAnnotation)")
    public void logExecutionPointcut() {
    }

    @Around("logExecutionPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // 打印入参，但过滤掉可能导致类型转换问题的对象
        Object[] safeArgs = filterRequestObjects(args);
        log.info("Entering method: {}.{} with arguments: {}", className, methodName, Arrays.toString(safeArgs));

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 打印出参
            log.info("Exiting method: {}.{} with result: {} in {} ms", className, methodName, result, executionTime);
        }

        return result;
    }

    /**
     * 过滤掉可能导致类型转换问题的请求对象
     *
     * @param args 原始参数数组
     * @return 安全的参数数组
     */
    private Object[] filterRequestObjects(Object[] args) {
        return Arrays.stream(args)
                .map(this::convertIfNecessary)
                .toArray();
    }

    /**
     * 转换可能导致问题的对象
     *
     * @param arg 原始参数
     * @return 转换后的参数
     */
    private Object convertIfNecessary(Object arg) {
        // 如果是MultipartFile类型，只返回文件名等基本信息
        if (arg instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) arg;
            return String.format("MultipartFile(name=%s, originalName=%s, size=%d, contentType=%s)",
                    file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType());
        }

        // 如果是ServletRequest相关对象，返回简单的标识
        if (arg instanceof HttpServletRequest || arg instanceof ServletRequestAttributes) {
            return arg.getClass().getSimpleName() + "(filtered)";
        }

        // 其他类型保持不变
        return arg;
    }
}