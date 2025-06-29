package com.example.vvpcommom;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheUtils {

    /**
     * 初始化缓存
     */
    private static final LoadingCache<String, List<Long>> CACHE = CacheBuilder.newBuilder()
            // 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
            .maximumSize(500)
            // 设置缓存在写入之后在设定时间后失效
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<Long>>() {
                @Override
                public List<Long> load(String key) {
                    // 处理缓存键不存在缓存值时的处理逻辑
                    return null;
                }
            });

    private static final LoadingCache<String, String> TOKENARG = CacheBuilder.newBuilder()
            // 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
            .maximumSize(50)
            // 设置缓存在写入之后在设定时间后失效
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    // 处理缓存键不存在缓存值时的处理逻辑
                    return null;
                }
            });

    public static void putTokenArg(String key, String value) {
        TOKENARG.put(key, value);
    }

    public static String getTokenArg(String key) {
        try {
            return TOKENARG.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static void removeTokenArg(String key) {
        TOKENARG.invalidate(key);
    }

    /**
     * 存入缓存
     *
     * @param key
     * @param value
     */
    public static void put(String key, List<Long> value) {
        CACHE.put(key, value);
    }

    public static void remove(String key) {
        CACHE.invalidate(key);
    }


    /**
     * 获取缓存
     *
     * @param key
     */
    public static List<Long> get(String key) {
        try {
            return CACHE.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return 是否存在key
     */
    public static boolean isExist(String key) {
        boolean isExist;
        try {
            isExist = CACHE.get(key) != null;
        } catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }
}
