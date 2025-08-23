package com.liuzd.soft.service.impl;

import com.liuzd.soft.annotation.StrategiesAnnotation;
import com.liuzd.soft.service.Strategies;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@Component
@Slf4j
public class StrategiesFactory {

    //策略key定义
    final static String STRATEGIES_A = "A";
    final static String STRATEGIES_B = "B";

    private List<Strategies> strategieList;

    public static final String GLOBAL_STRATEGIES_PREFIX = "GLOBAL_STRATEGIES_PREFIX_";

    public static final Map<String, Strategies> strategiesMap = new HashMap<>();

    @Autowired
    public StrategiesFactory(List<Strategies> objList) {
        this.strategieList = objList;
    }

    @PostConstruct
    public void init() {
        for (Strategies strategies : strategieList) {
            Class<?> targetClass = AopUtils.getTargetClass(strategies);

            //1.获取目标类上的目标注解（可判断目标类是否存在该注解）
            StrategiesAnnotation annotationInClass = AnnotationUtils.findAnnotation(targetClass, StrategiesAnnotation.class);
            if (annotationInClass == null) {
                log.error("目标类:{} 缺少注解", targetClass.getName());
                continue;
            }
            String strategyName = annotationInClass.name();
            if ("".equals(strategyName)) {
                log.error("目标类:{} 缺少注解 name 值", targetClass.getName());
                continue;
            }
            strategiesMap.put(GLOBAL_STRATEGIES_PREFIX + strategyName, strategies);
        }
    }
}
