package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 运费模板区域配置表
 */
@Data
@TableName("shipping_template_area")
public class PShippingTemplateAreaEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "`template_id`")
    private Long templateId;

    @TableField(value = "`area_codes`")
    private String areaCodes;

    @TableField(value = "`area_names`")
    private String areaNames;

    @TableField(value = "`first_unit`")
    private BigDecimal firstUnit;

    @TableField(value = "`first_fee`")
    private BigDecimal firstFee;

    @TableField(value = "`additional_unit`")
    private BigDecimal additionalUnit;

    @TableField(value = "`additional_fee`")
    private BigDecimal additionalFee;

    @TableField(value = "`free_shipping_amount`")
    private BigDecimal freeShippingAmount;

    @TableField(value = "`created_at`")
    private Timestamp createdAt;

    @TableField(value = "`updated_at`")
    private Timestamp updatedAt;
}