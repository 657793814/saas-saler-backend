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
@TableName("t_permission")
public class TPermissionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "`id`", type = IdType.AUTO)
    private Integer id;


    @TableField(value = "`code`")
    private String code;

    @TableField(value = "`name`")
    private String name;


    @TableField(value = "`path`")
    private String path;

    @TableField(value = "`icon`")
    private String icon;

    @TableField(value = "`parent_code`")
    private String parentCode;

    @TableField(value = "`enable`")
    private Integer enable;


    @TableField(value = "`order`")
    private Integer order;

}

