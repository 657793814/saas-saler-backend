package com.liuzd.soft.feign;

import com.liuzd.soft.interceptor.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: liuzd
 * @date: 2025/9/17
 * @email: liuzd2025@qq.com
 * @desc FeignClient 配置
 * name: consul注册的微服务名（基于consul的服务发现机制实现的负载均衡）
 */
@FeignClient(name = "saas-saler-admin-prod", configuration = FeignClientInterceptor.class)
@Component
public interface UserFeignClient {
    //todo 定义user微服务接口函数
    @GetMapping("/hello_world")
    String helloWorld();
}
