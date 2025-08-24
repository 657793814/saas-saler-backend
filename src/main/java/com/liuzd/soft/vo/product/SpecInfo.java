package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * sku 规格信息
 *
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class SpecInfo {

    @NotNull(message = "规格类型id不能为空")
    @JsonProperty("specId")
    private String specTypeId;  //因为可能时临时id ,有 tmp_前缀

    @NotBlank(message = "规格类型值不能为空")
    @JsonProperty("specName")
    private String specTypeName;

    @NotNull(message = "规格值id不能为空")
    @JsonProperty("valueId")
    private String specValueId;  //因为可能时临时id ,有 tmp_前缀

    @NotBlank(message = "规格值不能为空")
    @JsonProperty("value")
    private String specValueName;


}
