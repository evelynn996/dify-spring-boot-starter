/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Map utilities for common operations
 *
 */
@Slf4j
@UtilityClass
public class MapUtil {

    /**
     * 从 Map 中获取指定类型的值
     *
     * @param map   Map 对象
     * @param key   键
     * @param type  目标类型
     * @param <T>   目标类型
     * @return 指定类型的值，如果不存在、为 null 或类型不匹配则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
        if (map == null || key == null || type == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 从 Map 中获取字符串值
     *
     * @param map Map 对象
     * @param key 键
     * @return 字符串值，如果不存在或为 null 则返回 null
     */
    public static String getStr(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 从 Map 中获取 Integer 值
     *
     * @param map Map 对象
     * @param key 键
     * @return Integer 值，如果不存在、为 null 或转换失败则返回 null
     */
    public static Integer getInt(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to Integer: key={}, value={}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中获取 Long 值
     *
     * @param map Map 对象
     * @param key 键
     * @return Long 值，如果不存在、为 null 或转换失败则返回 null
     */
    public static Long getLong(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to Long: key={}, value={}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中获取 Double 值
     *
     * @param map Map 对象
     * @param key 键
     * @return Double 值，如果不存在、为 null 或转换失败则返回 null
     */
    public static Double getDouble(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to Double: key={}, value={}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中获取 BigDecimal 值
     *
     * @param map Map 对象
     * @param key 键
     * @return BigDecimal 值，如果不存在、为 null 或转换失败则返回 null
     */
    public static BigDecimal getBigDecimal(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            }
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to BigDecimal: key={}, value={}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中获取 Boolean 值
     *
     * @param map Map 对象
     * @param key 键
     * @return Boolean 值，如果不存在或为 null 则返回 null
     */
    public static Boolean getBoolean(Map<?, ?> map, Object key) {
        if (map == null || key == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
