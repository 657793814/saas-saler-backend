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
public class RefreshTokenInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String openid;
    private String tenantCode;
    private long timestamp;


}
