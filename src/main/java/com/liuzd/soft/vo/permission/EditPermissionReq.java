package com.liuzd.soft.vo.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/23
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class EditPermissionReq {

    private Integer id;

    @NotNull(message = "code不能为空")
    @NotBlank(message = "code不能为空")
    private String code;

    @NotNull(message = "name不能为空")
    @NotBlank(message = "name不能为空")
    private String name;

    @NotNull(message = "icon不能为空")
    @NotBlank(message = "icon不能为空")
    private String icon;

    @NotNull(message = "path不能为空")
    @NotBlank(message = "path不能为空")
    private String path;

    @JsonProperty("parentCode")
    private String parentCode;

    private Integer enable;


    @JsonProperty("order")
    private Integer order;
}
