package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.type.Decimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @NotNull(message = "销售价格不能为空")
    @DecimalMin(value = "0.01", message = "销售价格不能小于0.01")
    @JsonProperty("price")
    private Decimal salePrice;

    @NotNull(message = "成本价格不能为空")
    @DecimalMin(value = "0.01", message = "成本价格不能小于0.01")
    @JsonProperty("costPrice")
    private Decimal costPrice;

    @NotNull(message = "库存不能为空")
    @JsonProperty("stock")
    private Integer stock;

    @NotNull(message = "商品规格不能为空")
    @JsonProperty("specifications")
    private List<SpecInfo> specList;

}
