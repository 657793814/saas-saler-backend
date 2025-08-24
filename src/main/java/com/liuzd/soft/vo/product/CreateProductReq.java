package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
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
public class CreateProductReq {

    @NotBlank(message = "商品标题不能为空")
    @JsonProperty("title")
    private String name;

    @NotBlank(message = "商品简介不能为空")
    @JsonProperty("summary")
    private String desc;

    @NotBlank(message = "商品详情描述不能为空")
    @JsonProperty("description")
    private String detail;

    @NotNull(message = "商品图片不能为空")
    private List<String> imageUrls;

    @NotNull(message = "商品sku不能为空")
    private List<SkuInfo> skus;

}
