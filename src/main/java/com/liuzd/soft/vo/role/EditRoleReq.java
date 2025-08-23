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
public class EditRoleReq {

    @NotNull(message = "id不能为空")
    private Integer id;


    @NotNull(message = "desc不能为空")
    @NotBlank(message = "desc不能为空")
    private String desc;

    @NotNull(message = "enable不能为空")
    private Integer enable;
}
