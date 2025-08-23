package com.liuzd.soft.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class AddUserReq {

    @JsonProperty("user_code")
    @NotNull(message = "openid不能为空")
    @NotBlank(message = "openid不能为空")
    private String userCode;

    @JsonProperty("user_name")
    @NotNull(message = "user_name不能为空")
    @NotBlank(message = "user_name不能为空")
    private String userName;

    @NotNull(message = "mobile不能为空")
    @NotBlank(message = "mobile不能为空")
    private String mobile;

    @NotNull(message = "password不能为空")
    @NotBlank(message = "password不能为空")
    private String password;
    private Boolean enable = null;
}
