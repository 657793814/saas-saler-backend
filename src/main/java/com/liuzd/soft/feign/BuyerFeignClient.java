package com.liuzd.soft.feign;

import com.liuzd.soft.interceptor.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 调用微服务 saas-buyer-api-prod
 *
 * @author: liuzd
 * @date: 2025/9/17
 * @email: liuzd2025@qq.com
 * @desc FeignClient 配置
 * name: consul注册的微服务名（基于consul的服务发现机制实现的负载均衡）
 */
@FeignClient(name = "saas-buyer-api-prod", configuration = FeignClientInterceptor.class)
@Component
public interface BuyerFeignClient {
    //todo 定义 saas-buyer-api-prod 微服务接口
    @GetMapping("/test/hello")
    String helloWorld();
}
