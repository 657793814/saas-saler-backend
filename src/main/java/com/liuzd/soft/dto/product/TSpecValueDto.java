package com.liuzd.soft.dto.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class TSpecValueDto {
    private Integer id;
    private Integer specTypeId;
    private String value;
}
