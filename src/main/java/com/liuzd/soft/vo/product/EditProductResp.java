package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 商品详情响应结构
 *
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class EditProductResp {

    private Integer id;

    @JsonProperty("title")
    private String name;

    @JsonProperty("summary")
    private String desc;

    @JsonProperty("description")
    private String detail;

    @JsonProperty("imageUrls")
    private List<Map<String, String>> imageUrls;

    @JsonProperty("skus")
    private List<DetailSkuResp> skus;

}
