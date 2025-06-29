package com.example.vvpcommom;


import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;


public class SmartCopyUtil {


    /***
     * convert map to object ,see setObjectValue(obj, map)
     * @param map : key是对象的成员变量,其value就是成员变量的值
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     */
    public static Object convertMap2Obj(Map<String, Object> map, Class clazz) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, IllegalArgumentException {
        if (ValueWidget.isNullOrEmpty(map)) {
            return null;
        }
        Object obj = clazz.newInstance();
        setObjectValue(obj, map);
        return obj;
    }


    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        for (Field field : fieldList) {
            ReflectionUtils.makeAccessible(field);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if ("menuType".equals(fieldName)) {
                if ("M".equals(String.valueOf(value))) {
                    value = "目录";
                }
                if ("C".equals(String.valueOf(value))) {
                    value = "菜单";
                }
                if ("F".equals(String.valueOf(value))) {
                    value = "按钮";
                }
            }
            map.put(fieldName, value);
        }
        return map;
    }


    /***
     * 利用反射设置对象的属性值. 注意:属性可以没有setter 方法.
     *
     * @param obj
     * @param params
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setObjectValue(Object obj, Map<String, Object> params)
            throws SecurityException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        if (ValueWidget.isNullOrEmpty(obj)) {
            return;
        }
        if (ValueWidget.isNullOrEmpty(params)) {
            return;
        }
        Class<?> clazz = obj.getClass();
        for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
                    .next();
            String key = entry.getKey();
            Object propertyValue = entry.getValue();
            if (ValueWidget.isNullOrEmpty(propertyValue)) {
                continue;
            }
            Field name = getSpecifiedField(clazz, key);
            if (name != null) {
                ReflectionUtils.makeAccessible(name);
                if (name.equals("id")) {
                    name.set(obj, propertyValue);
                }
                name.set(obj, propertyValue);
            }
        }

    }


    private static Field getSpecifiedField(Class<?> clazz, String key) {
        try {
            return clazz.getDeclaredField(key.substring(0, 1).toLowerCase() + key.substring(1));
        } catch (NoSuchFieldException e) {
            try {
                return clazz.getSuperclass().getDeclaredField(key.substring(0, 1).toLowerCase() + key.substring(1));
            } catch (NoSuchFieldException noSuchFieldException) {
                throw new RuntimeException("no such file" + key);
            }
        }
    }


    private static class ValueWidget {
        public static boolean isNullOrEmpty(Map<String, Object> params) {
            return (params == null || params.size() == 0);
        }

        public static boolean isNullOrEmpty(Object obj) {
            return obj == null;
        }
    }
}

