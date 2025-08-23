package com.liuzd.soft.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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

        // 打印入参
        log.info("Entering method: {}.{} with arguments: {}", className, methodName, Arrays.toString(args));

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
}
