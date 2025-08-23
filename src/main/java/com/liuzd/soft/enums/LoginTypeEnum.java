package com.liuzd.soft.enums;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
public enum LoginTypeEnum{
    LOGIN_TYPE_USERNAME(1,"用户名密码登录"),
    LOGIN_TYPE_MOBILE(2,"手机号验证码登录"),
    ;
    private int code;
    private String desc;
    LoginTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
