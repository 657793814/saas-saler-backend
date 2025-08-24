package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class SkuResp {
    @JsonProperty("skuId")
    private Integer id;
    @JsonProperty("productId")
    private Integer productId;
    private String code;
    private String name;
    private String spec;
    private String imgUrl; //预览图
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private Integer stock;
    private int enable;
}
