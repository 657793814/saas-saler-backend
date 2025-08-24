package com.liuzd.soft.dto.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.type.Decimal;
import com.liuzd.soft.vo.product.SpecInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class PItemsDto {
    private Integer id;
    private Integer productId;
    private String img;
    private Decimal salePrice;
    private Decimal costPrice;
    private String specData;
    private SpecInfo specInfo;
    private Integer stock;
}
