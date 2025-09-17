package com.liuzd.soft.config;


import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: liuzd
 * @date: 2025/9/17
 * @email: liuzd2025@qq.com
 * @desc
 */
@Configuration
public class FeignConfig {

    /**
     * 配置Feign日志级别
     * NONE: 不记录任何日志
     * BASIC: 仅记录请求方法、URL、响应状态码及执行时间
     * HEADERS: 记录基本信息及请求/响应头
     * FULL: 记录请求和响应的正文及元数据
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 配置超时时间
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(5000, 10000); // 连接超时5秒，读取超时10秒
    }

    /**
     * 配置重试机制
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 2000, 3); // 初始间隔1秒，最大间隔2秒，最多重试3次
    }
}
