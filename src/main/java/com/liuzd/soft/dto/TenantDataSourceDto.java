package com.liuzd.soft.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 动态数据源DTO， 默认代码
 * USER:liuzd01
 * DATE:2023/9/14
 * EMAIL:liuzd2025@qq.com
 */
@Data
public class TenantDataSourceDto implements Serializable {

    /**
     * 数据源key, 目前用tenantCode去标识
     */
    private String tenantKey;

    /**
     * 数据库类型，用于识别连接不同的数据库的时候设置驱动的字段，0默认为jdbc
     */
    private Integer tenantType = 0;

    /**
     * 数据库连接URL
     */
    private String url;

    /**
     * 数据库连接用户名
     */
    private String username;

    /**
     * 数据库连接密码
     */
    private String password;

}
