package com.liuzd.soft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 服务间调用示例
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceCall {

    private final RestTemplate restTemplate;

    /**
     * @param serviceName 服务名称
     * @param path        API路径
     * @return 响应结果
     */
    public String callService(String serviceName, String path) {
        try {
            // 使用服务名称进行调用，由Ribbon实现负载均衡
            String url = "http://" + serviceName + path;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("调用服务失败, service: {}, path: {}", serviceName, path, e);
            throw e;
        }
    }

    /**
     * POST方式调用其他服务
     *
     * @param serviceName  服务名称
     * @param path         API路径
     * @param requestBody  请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public <T> T postToService(String serviceName, String path, Object requestBody, Class<T> responseType) {
        try {
            String url = "http://" + serviceName + path;

            ResponseEntity<T> response = restTemplate.postForEntity(url, requestBody, responseType);
            return response.getBody();
        } catch (Exception e) {
            log.error("POST调用服务失败, service: {}, path: {}", serviceName, path, e);
            throw e;
        }
    }
}
