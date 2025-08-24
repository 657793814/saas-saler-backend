package com.liuzd.soft.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class ProductPageResp {

    private Integer productId;
    private String code;
    private String name;
    private String imgUrl; //预览图
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private int enable;
    private List<SkuResp> children;

}
