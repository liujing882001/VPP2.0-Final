package com.example.vvpcommom.lock;

import java.lang.annotation.*;


@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * 【必须】redis锁 key的前缀
     */
    String key() default "";

    /**
     * 重试重获取锁的次数,默认0 不重试
     */
    int retry() default 0;

    /**
     * 占有锁的时间,避免程序宕机导致锁无法释放
     */
    int expired() default 60;

}
