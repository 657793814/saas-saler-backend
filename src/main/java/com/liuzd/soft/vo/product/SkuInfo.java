package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class SkuInfo {

    @JsonProperty("price")
    private BigDecimal salePrice;

    @JsonProperty("costPrice")
    private BigDecimal costPrice;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("specifications")
    private List<SpecInfo> specList;

}
