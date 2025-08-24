package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class SpecDataResp {

    private Integer specTypeId;
    private String specTypeName;
    private List<SpecValue> specValues;

}
