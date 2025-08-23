package com.liuzd.soft.vo.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class RefreshTokenReq {

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String openid;

    @JsonProperty("tenant_code")
    private String tenantCode;


}
