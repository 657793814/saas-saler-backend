package com.liuzd.soft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class TRolePermissionDto {

    @JsonProperty(value = "id")
    private Integer id;

    @JsonProperty(value = "role_id")
    private Integer roleId;


    @JsonProperty(value = "permission_id")
    private Integer permissionId;

}
