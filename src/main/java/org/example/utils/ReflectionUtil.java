package org.example.utils;

import org.example.utils.annotation.ReferenceColumn;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Component
public class ReflectionUtil {

    public static <T> List<ReferenceColumn> getAll(Class<T> clazz) {
        List<ReferenceColumn> referenceColumns = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ReferenceColumn) {
                    referenceColumns.add((ReferenceColumn) annotation);
                }
            }
        }
        return referenceColumns;
    }

    public static String execGetMethod(Object object, String filedName, boolean isJson) {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getMethod("get" + (filedName.charAt(0) + "").toUpperCase() + filedName.substring(1));
            Object obj = method.invoke(object);
            if (obj == null) {
                return null;
            }
            if (isJson) {
                return DataUtils.objToJsonOptimize(obj);
            }
            if (obj instanceof Instant) {
                return DateUtil.toStr(DateUtil.minusHour((Instant) obj, 7), "yyyy-MM-dd HH:mm:ss");
            }
            if (obj instanceof Date) {
                return DateUtil.toStr((Date) obj, "yyyy-MM-dd HH:mm:ss");
            }
            return String.valueOf(obj);
        } catch (Exception ex) {
            return null;
        }
    }
}
