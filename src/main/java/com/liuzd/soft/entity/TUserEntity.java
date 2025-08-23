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
@TableName("t_user")
public class TUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "`openid`", type = IdType.INPUT)
    private String openid;


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

