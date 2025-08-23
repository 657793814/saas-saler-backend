package com.liuzd.soft.exception;

import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常捕获
 *
 * @author :liuzd
 * date:2023/9/18
 * email:liuzd2025@qq.com
 */
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    /**
     * @Author
     * @Description 全局异常处理捕获参数不合法异常
     * @Param [e]
     **/
    @ExceptionHandler({BindException.class, IllegalArgumentException.class, ConstraintViolationException.class})
    public ResultMessage<Object> BindException(BindException e) {
        return ResultMessage.fail(RetEnums.PARAMETER_NOT_VALID.getCode(), RetEnums.PARAMETER_NOT_VALID.getMessage());
    }

    /**
     * @MethodName: CustomExceptionHandler
     * @Params: [e]
     * @Description: 自定义异常处理
     */
    @ExceptionHandler(MyException.class)
    public ResultMessage<Object> CustomExceptionHandler(MyException e) {
        return ResultMessage.fail(e.getCode(), e.getMessage());
    }

    /**
     * @MethodName: ExceptionHandler
     * @Params: [e]
     * @Description: 其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResultMessage<Object> ExceptionHandler(Exception e) {
        return ResultMessage.fail(RetEnums.FAIL.getCode(), RetEnums.FAIL.getMessage());
    }

}
