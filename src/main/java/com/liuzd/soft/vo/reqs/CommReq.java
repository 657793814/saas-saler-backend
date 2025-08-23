package com.liuzd.soft.vo.reqs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.consts.GlobalConstant;
import lombok.Data;

/**
 * 公共请求体
 *
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class CommReq {

    @JsonProperty(GlobalConstant.REQUEST_PARAM_TOKEN_KEY)
    private String token;
    @JsonProperty(GlobalConstant.REQUEST_PARAM_RAND_STR_KEY)
    private String randStr;
    @JsonProperty(GlobalConstant.REQUEST_PARAM_SIGN_KEY)
    private String sign;
    @JsonProperty(GlobalConstant.REQUEST_PARAM_TIMESTAMP_KEY)
    private String timestamp;

    @JsonProperty(GlobalConstant.REQUEST_PARAM_TENANT_CODE_KEY)
    private String tenantCode;


}
