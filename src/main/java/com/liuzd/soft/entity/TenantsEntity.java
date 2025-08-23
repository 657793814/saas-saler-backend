package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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
@TableName("tenants")
public class TenantsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "`tenant_id`", type = IdType.INPUT)
    private String tenantId;

    @TableField(value = "`tenant_name`")
    private String tenantName;

    @TableField(value = "`tenant_code`")
    private String tenantCode;

    @TableField(value = "`owner_area`")
    private String ownerArea;

    @TableField(value = "`db_name`")
    private String dbName;

    @TableField(value = "`instance_id`")
    private String instanceId;

    @TableField(value = "`admin_name`")
    private String adminName;

    @TableField(value = "`admin_mail`")
    private String adminMail;

    @TableField(value = "`init_pwd`")
    private String initPwd;

    @TableField(value = "`create_time`")
    private Date createTime;

    @TableField(value = "`update_time`")
    private Date updateTime;

    @TableField(value = "`is_enable`")
    private Integer isEnable;


}

