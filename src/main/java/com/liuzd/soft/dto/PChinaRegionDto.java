package com.liuzd.soft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 中国省市区 dto类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PChinaRegionDto {

    private Integer id;

    private String code;

    private String name;

    private String parentCode;

    private Integer level;

    private String pinyin;

    private String initial;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}