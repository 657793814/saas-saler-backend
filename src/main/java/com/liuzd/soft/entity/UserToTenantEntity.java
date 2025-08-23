package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * p_tenants 表实体类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_to_tenant")
public class UserToTenantEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(value = "`openid`")
    private String openId;

    @TableField(value = "`union_id`")
    private String unionId;

    @TableField(value = "`tenant_id`")
    private String tenantId;

    @TableField(value = "`enable`")
    private Integer enable;

}

