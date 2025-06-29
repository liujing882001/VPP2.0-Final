
package com.example.vvpcommom.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalUtil {
    private static ThreadLocal<Map<String, Object>> tl = new ThreadLocal<>();

    private ThreadLocalUtil() {

    }
    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public static synchronized <T> T put(String key, T value) {
        if (tl.get() == null)
            tl.set(new ConcurrentHashMap<>());
        Object put = tl.get().put(key, value);
        return (T) put;
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public static <T> T get(String key) {
        Map<String, Object> map = tl.get();
        if (map != null)
            return (T) map.get(key);
        return null;
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public static <T> T get(String key, Class<T> t) {
        Map<String, Object> map = tl.get();
        if (map != null)
            return (T) map.get(key);
        return null;
    }

    /**
     * 清除某个key
     *
     * @param key
     * @return
     */
    public static synchronized <T> T remove(String key) {
        Map<String, Object> map = tl.get();
        if (map != null)
            return (T) map.remove(key);
        return null;
    }

    /**
     * 清空所有的缓存数据
     */
    public static synchronized void clear() {
        tl.remove();
    }

}
