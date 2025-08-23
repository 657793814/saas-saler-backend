package com.liuzd.soft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * p_tenants dto类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToTenantDto {

    private String tenantId;
    private String unionId;
    private String openId;
    private int enable;

}

