package com.liuzd.soft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * ps:此项目使用openfeign，不需要配置RestTemplate，这里只做示例用
 */
@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connect-timeout:5000}")
    private int connectTimeout;

    /**
     * 配置支持负载均衡的RestTemplate
     *
     * @return
     * @LoadBalanced 支持负载均衡
     * @SentinelRestTemplate 注解的属性支持限流(blockHandler, blockHandlerClass)和降级(fallback, fallbackClass)的处理。
     * 其中 blockHandler 或 fallback 属性对应的方法必须是对应 blockHandlerClass 或 fallbackClass 属性中的静态方法。
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
