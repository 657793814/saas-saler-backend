package com.liuzd.soft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 中心库商品 sku
 *
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@TableName("p_items")
public class PItemsEntity {

    @TableId(value = "`id`", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "`product_id`")
    private Integer productId;


    @TableField(value = "`code`")
    private String code;

    @TableField(value = "`img`")
    private String img;

    @TableField(value = "`sale_price`")
    private BigDecimal salePrice;

    @TableField(value = "`cost_price`")
    private BigDecimal costPrice;

    @TableField(value = "`spec_data`")
    private String specData;

    @TableField(value = "`stock`")
    private Integer stock;
    
    @TableField(value = "`enable`")
    private Integer enable;

}
