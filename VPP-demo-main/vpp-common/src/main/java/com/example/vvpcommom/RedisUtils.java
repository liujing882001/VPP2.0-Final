package com.example.vvpcommom;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author zph
 * @date 2022-09-01
 */
@Component
@Data
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    //设置默认过期时间
    private final static int DEFAULT_LOCK_EXPIRY_TIME = 60;

    private final static String LOCK_PREFIX = "dis_lock_";

    @Resource
    private RedisTemplate redisTemplate;

    public RedisUtils(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 获取分布式锁
    public boolean acquireLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Boolean result = ops.setIfAbsent(LOCK_PREFIX + lockKey, lockValue, timeout, unit);
        return result != null && result;
    }

    // 释放分布式锁
    public boolean releaseLock(String lockKey, String lockValue) {
        String key = LOCK_PREFIX + lockKey;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.eval(script.getBytes(), ReturnType.INTEGER, 1, key.getBytes(), lockValue.getBytes())
        );
        return "1".equals(result.toString());
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean add(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("普通缓存放入", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean add(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                add(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("普通缓存放入", e);
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(Object key) {

        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("判断key是否存在", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param timeout 时间(秒)
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, timeUnit);
            }
            return true;
        } catch (Exception e) {
            logger.error("指定缓存失效时间", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 是否能够得到redis的key对应数据值
     *
     * @param key
     * @return
     */
    public synchronized Boolean existOrNot(String key, Object value) {
        if (hasKey(key)) {
            return true;
        } else {
            add(key, value, DEFAULT_LOCK_EXPIRY_TIME, TimeUnit.SECONDS);
            return false;
        }
    }

    public synchronized Boolean existOrNot(String key) {
        return existOrNot(key, "1");
    }
}

