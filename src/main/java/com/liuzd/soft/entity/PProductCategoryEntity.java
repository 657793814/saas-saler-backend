package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 商品分类表
 */
@Data
@TableName("product_category")
public class PProductCategoryEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "`parent_id`")
    private Integer parentId;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "`sort_order`")
    private Integer sortOrder;

    @TableField(value = "`icon_url`")
    private String iconUrl;

    @TableField(value = "`image_url`")
    private String imageUrl;

    @TableField(value = "`description`")
    private String description;

    @TableField(value = "`seo_title`")
    private String seoTitle;

    @TableField(value = "`seo_keywords`")
    private String seoKeywords;

    @TableField(value = "`seo_description`")
    private String seoDescription;

    @TableField(value = "`created_at`")
    private Timestamp createdAt;

    @TableField(value = "`updated_at`")
    private Timestamp updatedAt;
}