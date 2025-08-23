package com.liuzd.soft.utils;

import java.security.SecureRandom;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
public class TokenUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateStr(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateToken() {
        return generateStr(32);
    }

    public static String generateRefreshToken() {
        return generateStr(64);
    }

    public static String generateRandStr() {
        return generateStr(6);
    }
}
