package com.liuzd.soft.vo.rets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.enums.RetEnums;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultMessage<T> {

    //java对象转json对象映射
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String message;
    @JsonProperty("data")
    private T Data;

    public ResultMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        Data = data;
    }

    public static <T> ResultMessage<T> success(T data) {
        return new ResultMessage<>(RetEnums.SUCCESS.getCode(), RetEnums.SUCCESS.getMessage(), data);
    }

    public static <T> ResultMessage<T> fail(Integer code, String message) {
        return new ResultMessage<>(code, message, null);
    }

}
