package com.liuzd.soft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RefreshScope
@EnableDiscoveryClient
@EnableRabbit
@MapperScan("com.liuzd.soft.dao")
@EnableFeignClients
public class SaasSalerAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasSalerAdminApplication.class, args);
    }

}
