package com.liuzd.soft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * p_tenants dto类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TUserDto {

    private String openid;

    private String uname;

    private String userCode;

    private String pwd;

    private String salt;

    private String mobile;

    private Integer enable;

    private Date createTime;

}

