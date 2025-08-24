package com.liuzd.soft.dto.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class PProductsDto {
    private Integer id;
    private String code;
    private String name;
    private String desc;
    private String detail;
    private String mainImg; //商品主图
    private List<String> imgUrls;
    private Integer enable;
}
