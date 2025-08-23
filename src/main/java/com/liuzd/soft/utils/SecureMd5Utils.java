package com.liuzd.soft.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
public class SecureMd5Utils {

    public static String md5WithSalt(String password, String salt) {
        return DigestUtils.md5Hex(password + salt);
    }

    // 生成随机盐值
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
