package com.liuzd.soft.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: liuzd
 * @date: 2025/8/18
 * @email: liuzd2025@qq.com
 * @desc
 */
public class ThreadContextHolder {
    private static final ThreadLocal<Map<String, Object>> CONTEXT_HOLDER = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    public static void put(String key, Object value) {
        CONTEXT_HOLDER.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT_HOLDER.get().get(key);
    }

    public static String getTenantCode() {
        return (String) CONTEXT_HOLDER.get().get("tenantCode");
    }

    public static void putTenantCode(String tenantCode) {
        CONTEXT_HOLDER.get().put("tenantCode", tenantCode);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
