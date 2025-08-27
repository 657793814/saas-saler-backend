package com.liuzd.soft.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置一些自定义的开关
 *
 * @author: liuzd
 * @date: 2025/8/27
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@Component
@ConfigurationProperties
public class ProjectProperties {

    @Value("${spring.elasticsearch.enabled}")
    private boolean esEnabled;
}
