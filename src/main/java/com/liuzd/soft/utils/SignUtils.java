package com.liuzd.soft.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
public class SignUtils {

    public static String sign(String randStr, long timestamp, String secretKey){
        return DigestUtils.md5Hex(randStr + timestamp + secretKey);
    }

    public static boolean verify(String randStr, long timestamp, String sign, String secretKey){
        return sign(randStr, timestamp, secretKey).equals(sign);
    }

}
