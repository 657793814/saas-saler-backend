/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */
package com.liuzd.soft.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * MyBatis分页配置
 *
 * @author sundy
 */
@Slf4j
@Configuration
public class MyBatisConfig {

    /**
     * 配置文件yml中的默认数据源 config库
     *
     * @return
     */
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX + ".datasource.default")
    public DataSource getDefaultDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 配置文件yml中的日志数据源 log库
     *
     * @return
     */
    @Bean(name = "logDataSource")
    @ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX + ".datasource.log")
    public DataSource getLogDataSource() {
        return DruidDataSourceBuilder.create().build();
    }


    /**
     * MyBatis Plus 分页插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
