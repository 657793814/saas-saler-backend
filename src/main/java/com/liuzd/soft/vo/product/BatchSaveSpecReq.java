package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: liuzd
 * @date: 2025/8/25
 * @email: liuzd2025@qq.com
 * @desc
 */
@JsonSerialize
@Data
public class BatchSaveSpecReq {

    //在规格类型下批量新增有此值
    @JsonProperty(value = "specTypeId")
    private Integer specTypeId;

    //完全新增规格类型和规格值时有此值
    @JsonProperty(value = "specTypeName")
    private String specTypeName;

    // [{specValue: "12"}, {specValue: "13"}]
    @JsonProperty(value = "specValues")
    @NotNull
    private List<Map<String, String>> specValues;

}
