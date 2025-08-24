package com.liuzd.soft.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
public class DateUtils {

    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 返回格式化的当前日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String getCurrentDateTimeString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * 将毫秒时间戳格式化为日期字符串 (yyyy-MM-dd)
     *
     * @param timestamp
     * @return
     */
    public static String formatDateString(long timestamp) {
        Date date = new Date(timestamp);
        return date.toString();
    }

    /**
     * 将毫秒时间戳格式化为日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     *
     * @param timestamp
     * @return
     */
    public static String formatDateTimeString(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 将Date对象格式化为字符串 (yyyy-MM-dd)
     *
     * @param date
     * @return
     */
    public static String formatDateString(Date date) {
        return date.toString(); // java.sql.Date的toString()方法已经返回yyyy-MM-dd格式
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDate());
        System.out.println(getCurrentDateTimeString());
        System.out.println(formatDateString(System.currentTimeMillis()));
        System.out.println(formatDateTimeString(System.currentTimeMillis()));
    }
}

