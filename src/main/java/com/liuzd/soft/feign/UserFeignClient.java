package com.liuzd.soft.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: liuzd
 * @date: 2025/9/17
 * @email: liuzd2025@qq.com
 * @desc
 */
@FeignClient(name = "user-service", url = "http://saas-user")
public interface UserFeignClient {
    //todo 定义user微服务接口函数
}
