package com.liuzd.soft.utils;

import com.liuzd.soft.consts.GlobalConstant;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
public class IdUtils {

    /**
     * 生成定长数字字符串，左补0
     *
     * @param number 数字
     * @param length 目标长度
     * @return 定长字符串
     */
    public static String padNumber(long number, int length) {
        String format = "%0" + length + "d";
        return String.format(format, number);
    }

    public static String generateOpenId(long number) {
        return GlobalConstant.OPENID_PREFIX + padNumber(number, 23);
    }

    public static String generateOpenId() {
        return GlobalConstant.OPENID_PREFIX + TokenUtils.generateStr(23);
    }

    public static String generateUnionId(long number) {
        return GlobalConstant.UNION_ID_PREFIX + padNumber(number, 23);
    }

    public static String generateUnionId() {
        return GlobalConstant.UNION_ID_PREFIX + TokenUtils.generateStr(23);
    }

    public static String generateProductCode() {
        return GlobalConstant.PRODUCT_ID_PREFIX + TokenUtils.generateStr(7) + System.currentTimeMillis();
    }

    public static String generateSkuCode() {
        return GlobalConstant.SKU_ID_PREFIX + TokenUtils.generateStr(7) + System.currentTimeMillis();
    }
}
