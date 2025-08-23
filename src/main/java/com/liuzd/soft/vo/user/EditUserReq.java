package com.liuzd.soft.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class EditUserReq {

    @NotNull(message = "openid不能为空")
    @NotBlank(message = "openid不能为空")
    private String openid;

    @JsonProperty("user_name")
    private String userName;
    private String mobile;
    private String password;
    private Boolean enable = null;
}
