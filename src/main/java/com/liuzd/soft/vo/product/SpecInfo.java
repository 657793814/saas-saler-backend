package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @JsonProperty("specId")
    private String specTypeId;  //因为可能时临时id ,有 tmp_前缀

    @JsonProperty("specName")
    private String specTypeName;

    @JsonProperty("valueId")
    private String specValueId;  //因为可能时临时id ,有 tmp_前缀

    @JsonProperty("value")
    private String specValueName;


}
