package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.vo.page.PageRequest;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class SpecDataPageReq extends PageRequest {
    @JsonProperty(value = "name", defaultValue = "")
    private String name;
}
