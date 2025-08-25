package com.liuzd.soft.enums;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
public enum RetEnums {
    SUCCESS(0, "成功"),
    FAIL(-1, "失败"),

    /**
     * 1xxxxx为参数错误
     */
    PARAMETER_NOT_VALID(100000, "参数错误"),
    UNKNOWN_TENANT_REQUEST(100001, "租户参数错误"),

    /**
     * 2xxxxx为业务错误
     */
    BUSINESS_ERROR(200000, "业务错误"),
    TENANT_NOT_EXISTS(200001, "租户不存在"),
    TENANT_DISABLE(200002, "租户不可用"),
    SERVICE_OFF(200003, "服务关闭"),
    VERIFY_SIGN_FAIL(200004, "token签名校验失败"),
    VERIFY_SIGN_TIMESTAMP_FAIL(200005, "请求超过有效期"),
    LOGIN_EXPIRE(200006, "登录已失效"),
    UNKNOWN_LOGIN_TYPE(200007, "未知的登录类型"),
    USER_NOT_EXIST(200008, "用户不存在"),
    USER_EXIST(200009, "用户已存在"),
    USERNAME_OR_PWD_ERROR(200010, "用户账号密码错误"),
    ROLE_NOT_EXIST(200011, "角色不存在"),
    PERMISSION_NOT_EXIST(200012, "权限点不存在"),
    NO_PERMISSION(200013, "您没有操作权限"),
    PRODUCT_NOT_EXIST(200014, "商品不存在"),
    PRODUCT_EDIT_ERROR(200015, "编辑商品发生异常"),
    SPEC_TYPE_NOT_EXIST(200016, "规格类型不存在"),
    SPEC_VALUE_NOT_EXIST(200017, "规格值不存在"),
    SKU_NOT_EXIST(200018, "sku不存在"),
    ;
    private final Integer code;
    private final String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    RetEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
