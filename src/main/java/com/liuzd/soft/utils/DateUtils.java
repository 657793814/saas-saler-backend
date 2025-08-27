package com.liuzd.soft.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
public class DateUtils {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return sdf.format(date);
    }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
        return dateTime.format(formatter);
    }
    
}

