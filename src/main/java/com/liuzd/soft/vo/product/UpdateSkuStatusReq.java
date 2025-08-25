package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/25
 * @email: liuzd2025@qq.com
 * @desc
 */
@JsonSerialize
@Data
public class UpdateSkuStatusReq {

    @JsonProperty(value = "skuId")
    @NotNull
    private Integer skuId;

    @JsonProperty(value = "enable")
    @NotNull
    private Integer enable;


}
