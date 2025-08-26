package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 中国省市区表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("china_region")
public class PChinaRegionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "code")
    private String code;

    @TableField(value = "name")
    private String name;

    @TableField(value = "`parent_code`")
    private String parentCode;

    @TableField(value = "`level`")
    private Integer level;

    @TableField(value = "`pinyin`")
    private String pinyin;

    @TableField(value = "`initial`")
    private String initial;

    @TableField(value = "`created_at`")
    private Timestamp createdAt;

    @TableField(value = "`updated_at`")
    private Timestamp updatedAt;
}