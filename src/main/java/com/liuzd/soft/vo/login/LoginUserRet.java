package com.liuzd.soft.vo.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUserRet<T> {


    @JsonProperty("union_id")
    private String unionId;

    @JsonProperty("tenant_code")
    private String tenantCode;

    private String openid;

    private String uname;

    @JsonProperty("user_code")
    private String userCode;


    private String mobile;

    private Integer enable;

}