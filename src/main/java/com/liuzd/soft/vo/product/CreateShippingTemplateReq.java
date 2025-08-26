package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class CreateShippingTemplateReq {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("templateName")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @JsonProperty("valuationType")
    private Integer valuationType;

    @JsonProperty("additionalFee")
    @DecimalMin(value = "0.01")
    private BigDecimal additionalFee;

    @JsonProperty("firstFee")
    @DecimalMin(value = "0.01")
    private BigDecimal firstFee;

    @JsonProperty("freeShippingAmount")
    private BigDecimal freeShippingAmount;

    @JsonProperty("status")
    private Integer status;

}
