package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 运费模板表
 */
@Data
@TableName("shipping_template")
public class PShippingTemplateEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "`template_name`")
    private String templateName;

    @TableField(value = "`tenant_code`")
    private String tenantCode;

    @TableField(value = "`valuation_type`")
    private Integer valuationType;

    @TableField(value = "`first_fee`")
    private BigDecimal firstFee;

    @TableField(value = "`additional_fee`")
    private BigDecimal additionalFee;

    @TableField(value = "`free_shipping_amount`")
    private BigDecimal freeShippingAmount;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "`sort_order`")
    private Integer sortOrder;

    @TableField(value = "`created_at`")
    private Timestamp createdAt;

    @TableField(value = "`updated_at`")
    private Timestamp updatedAt;
}