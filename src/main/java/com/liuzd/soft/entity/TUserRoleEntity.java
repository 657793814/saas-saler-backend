package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("t_user_role")
public class TUserRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "`id`", type = IdType.AUTO)
    private String id;

    @TableField(value = "`openid`")
    private String openid;


    @TableField(value = "`role_id`")
    private Integer roleId;


}

