package com.liuzd.soft.vo.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.vo.reqs.CommReq;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class LoginReq extends CommReq {

    @JsonProperty("login_type")
    private int loginType;
    private String mobile;
    private String username;
    private String password;  //前端加密一次之后的，服务端在此基础上加盐值再加密对比
    @JsonProperty("sms_code")
    private String smsCode;

}
