package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.utils.common.Constants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataUtils {

    public static <T> String objToJsonOptimize(T t) {
        String jsonStr = objToJson(t);
        if (jsonStr != null) {
            return jsonStr.replaceAll("\n", "").replaceAll("\r", "");
        }
        return null;
    }

    public static <T> String objToJson(T t) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isNullOrEmpty(final Object object) {
        return object == null || object.toString().isEmpty();
    }

    public static Double safeToDouble(Object obj) {
        return safeToDouble(obj, null);
    }

    public static Double safeToDouble(Object obj, Double defaultValue) {
        if (obj instanceof Double)
            return (Double) obj;
        try {
            return Double.valueOf(obj.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static Long safeToLong(Object obj) {
        return safeToLong(obj, null);
    }

    public static Long safeToLong(Object obj, Long defaultValue) {
        if (obj instanceof Double)
            return (Long) obj;
        try {
            return Long.valueOf(obj.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }


    public static String makeLikeStr(String str) {
        if (StringUtils.isEmpty(str)) return str;
        str = str.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("\\t", "\\\\t")
                .replace("\\n", "\\\\n")
                .replace("\\n", "\\\\n")
                .replace("\\z", "\\\\z")
                .replace("\\b", "\\\\b")
                .replace("&", Constants.DEFAULT_ESCAPE_CHAR + "&")
                .replace("%", Constants.DEFAULT_ESCAPE_CHAR + "%")
                .replace("_", Constants.DEFAULT_ESCAPE_CHAR + "_");
        return "%" + str + "%";
    }
}
