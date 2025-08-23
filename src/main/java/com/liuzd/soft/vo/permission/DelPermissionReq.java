package com.liuzd.soft.vo.permission;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/23
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@JsonSerialize
public class DelPermissionReq {

    private Integer id;
    
}
