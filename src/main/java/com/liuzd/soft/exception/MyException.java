package com.liuzd.soft.exception;

import com.liuzd.soft.enums.RetEnums;
import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */

@Data
public class MyException extends RuntimeException {

    private Integer code;
    private String msg;

    public MyException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public MyException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 使用枚举code及msg
     *
     * @param ret
     */
    public MyException(RetEnums ret) {
        super(ret.getMessage());
        this.code = ret.getCode();
    }

    /**
     * 枚举code, msg自定义
     *
     * @param ret
     * @param message
     */
    public MyException(RetEnums ret, String message) {
        super(message);
        this.code = ret.getCode();
    }

    public static MyException exception(RetEnums ret) {
        return new MyException(ret);
    }

    public static MyException exception(RetEnums ret, String msg) {
        return new MyException(ret, msg);
    }

}
