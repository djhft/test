package com.project.finance.util;

import tools.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper M = new ObjectMapper();
    public static String toJson(Object o) {
        return M.writeValueAsString(o);
    }
    public static <T> T fromJson(String s, Class<T> cls) {
        try {
            return M.readValue(s, cls);
        } catch (Exception e) { return null; }
    }
}
