package com.project.finance.util;

//import tools.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper M = new ObjectMapper();
    public static String toJson(Object o) throws JsonProcessingException {
        return M.writeValueAsString(o);
    }
    public static <T> T fromJson(String s, Class<T> cls) {
        try {
            return M.readValue(s, cls);
        } catch (Exception e) { return null; }
    }
}
