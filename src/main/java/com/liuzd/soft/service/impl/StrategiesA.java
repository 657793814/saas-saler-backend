package com.liuzd.soft.service.impl;

import com.liuzd.soft.annotation.StrategiesAnnotation;
import com.liuzd.soft.service.Strategies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@Component
@Slf4j
@StrategiesAnnotation(name = StrategiesFactory.STRATEGIES_A)
public class StrategiesA implements Strategies {
    @Override
    public void doSomething() {
        log.info("StrategiesA exec");
    }
}
