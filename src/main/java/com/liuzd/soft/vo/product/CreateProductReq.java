package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
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
public class CreateProductReq {

    //编辑商品时有值
    private Integer id;

    @JsonProperty("title")
    @NotBlank(message = "标题不能为空")
    private String name;

    @JsonProperty("summary")
    @NotBlank(message = "简介不能为空")
    private String desc;

    @JsonProperty("description")
    private String detail;

    @JsonProperty("imageUrls")
    private List<String> imageUrls;

    @JsonProperty("skus")
    private List<SkuInfo> skus;

}
