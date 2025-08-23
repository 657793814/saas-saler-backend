package com.liuzd.soft.vo.role;

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
public class AddRoleReq {

    @NotNull(message = "name不能为空")
    @NotBlank(message = "name不能为空")
    private String name;

    @NotNull(message = "desc不能为空")
    @NotBlank(message = "desc不能为空")
    private String desc;

    @NotNull(message = "enable不能为空")
    private Integer enable;
}
