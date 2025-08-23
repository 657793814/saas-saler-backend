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
public class UsersDto {

    private String unionId;

    private String userCode;

    private String uname;

    private String pwd;

    private String salt;

    private String mobile;

    private Integer enable;

    private Date createTime;

}

