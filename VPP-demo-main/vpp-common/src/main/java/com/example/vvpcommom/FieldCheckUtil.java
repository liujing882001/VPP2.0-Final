package com.example.vvpcommom;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

public class FieldCheckUtil {
    private static final Logger logger = LoggerFactory.getLogger(FieldCheckUtil.class);

    /**
     * 判断对象中属性值是否全为空
     *
     * @param object
     * @return
     */
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }

        boolean num = true;

        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                ReflectionUtils.makeAccessible(f);
                if (f.getName().equals("uploaded")) {
                    continue;
                }

                if (f.get(object) != null && !StringUtils.isEmpty(f.get(object).toString())) {
                    if (f.get(object) instanceof Double) {
                        num = num && ((Double) f.get(object)).doubleValue() == 0d;
                    }

                    return false;
                }

                return num;


            }

        } catch (Exception e) {
            logger.error("Error checking object fields", e);
        }

        return true;
    }

    public static boolean checkStringNotEmpty(String field) {
        return field != null && !"".equals(field.trim());
    }

    public static boolean checkListNotEmpty(List field) {
        return field != null && field.size() > 0;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void set(Object target, String fieldName, Object value) {
        try {
            for (Field f : target.getClass().getDeclaredFields()) {
                ReflectionUtils.makeAccessible(f);
                if (f.getName().equals(fieldName)) {
                    if (f.getType() == Double.class) {
                        f.set(target, new BigDecimal((String) value).doubleValue());
                    }

                    if (f.getType() == Integer.class) {
                        f.set(target, new BigDecimal((String) value).intValue());
                    }

                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error setting field {} on object {}", fieldName, target.getClass().getSimpleName(), e);
        }
    }
}
