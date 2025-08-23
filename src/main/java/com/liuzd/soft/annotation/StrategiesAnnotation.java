package com.liuzd.soft.annotation;

import java.lang.annotation.*;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface StrategiesAnnotation {
    String name() default "";
}
