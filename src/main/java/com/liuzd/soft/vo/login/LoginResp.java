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
public class LoginResp<T> {


    @JsonProperty("token")
    private String token;  //登陆凭证 时效7天
    @JsonProperty("refresh_token")
    private String refreshToken;  //用于刷新token,时效30天
    @JsonProperty("rand_str")
    private String randStr;  //与token同步生成和更新,前端用于请求加密生成，后端单独存储用于验证，不在后续接口请求中传输
    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("user_info")
    private T userInfo;  //用户信息

}
