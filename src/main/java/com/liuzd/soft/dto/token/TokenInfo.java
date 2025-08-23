package com.liuzd.soft.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tenantCode;
    private String openid;
    private String userCode;
    private String userName;
    private String randStr;
    private Long timestamp;


}
