package com.project.finance.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonUtil {
    private static final ObjectMapper M = new ObjectMapper()
            .registerModule(new JavaTimeModule())  // 支持Java 8日期时间类型
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 禁用时间戳格式

    // 基础方法
    public static String toJson(Object o) throws JsonProcessingException {
        return M.writeValueAsString(o);
    }

    public static <T> T fromJson(String s, Class<T> cls) {
        try {
            return M.readValue(s, cls);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增方法：带格式化的JSON输出
    public static String toPrettyJson(Object o) throws JsonProcessingException {
        return M.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }

    // 新增方法：解析为List
    public static <T> List<T> fromJsonToList(String json, Class<T> elementType) {
        try {
            return M.readValue(json, M.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (Exception e) {
            return null;
        }
    }

    // 新增方法：解析为Map
    public static Map<String, Object> fromJsonToMap(String json) {
        try {
            return M.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    // 新增方法：安全的JSON转换，返回Optional
    public static <T> Optional<T> safeFromJson(String json, Class<T> cls) {
        try {
            return Optional.ofNullable(M.readValue(json, cls));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 新增方法：对象转换
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        try {
            return M.convertValue(fromValue, toValueType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // 新增方法：深度复制对象
    public static <T> T deepCopy(Object source, Class<T> targetType) {
        try {
            String json = M.writeValueAsString(source);
            return M.readValue(json, targetType);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增方法：判断字符串是否为有效JSON
    public static boolean isValidJson(String json) {
        try {
            M.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 新增方法：获取JSON节点的值
    public static String getNodeValue(String json, String nodePath) {
        try {
            return M.readTree(json).at(nodePath).asText();
        } catch (Exception e) {
            return null;
        }
    }

    // 新增方法：合并两个JSON对象
    public static String mergeJson(String json1, String json2) {
        try {
            Map<String, Object> map1 = fromJsonToMap(json1);
            Map<String, Object> map2 = fromJsonToMap(json2);
            if (map1 != null && map2 != null) {
                map1.putAll(map2);
                return toJson(map1);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
