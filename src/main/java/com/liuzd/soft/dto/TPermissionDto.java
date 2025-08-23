package com.liuzd.soft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.entity.TPermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/22
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class TPermissionDto {

    @JsonProperty(value = "id")
    private Integer id;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "path")
    private String path;

    @JsonProperty(value = "icon")
    private String icon;

    @JsonProperty(value = "parentCode")
    private String parentCode;

    @JsonProperty(value = "enable")
    private Integer enable;

    @JsonProperty(value = "roles")
    private List<String> roles;

    @JsonProperty(value = "children")
    private List<TPermissionDto> children;


    public TPermissionDto(TPermissionEntity tPermissionEntity) {
        this.id = tPermissionEntity.getId();
        this.code = tPermissionEntity.getCode();
        this.name = tPermissionEntity.getName();
        this.path = tPermissionEntity.getPath();
        this.icon = tPermissionEntity.getIcon();
        this.enable = tPermissionEntity.getEnable();
        this.parentCode = tPermissionEntity.getParentCode();

    }
}
