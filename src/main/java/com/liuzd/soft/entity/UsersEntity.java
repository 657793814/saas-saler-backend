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
@TableName("users")
public class UsersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(value = "`union_id`")
    private String unionId;

    @TableField(value = "`user_code`")
    private String userCode;

    @TableField(value = "`uname`")
    private String userName;


    @TableField(value = "`mobile`")
    private String mobile;


    @TableField(value = "`pwd`")
    private String password;

    @TableField(value = "`salt`")
    private String salt;

    @TableField(value = "`enable`")
    private Integer enable;

}

