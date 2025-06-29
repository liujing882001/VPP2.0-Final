package com.example.vvpcommom.lock;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisLockPointcut {

    private static final Logger log = LoggerFactory.getLogger(RedisLockPointcut.class);

    @Resource
    private RedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.example.vvpcommom.lock.RedisLock)")
    public void redisLockPointCut() {
    }

    @Around("redisLockPointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Method method = currentMethod(proceedingJoinPoint);
        //获取到方法的注解对象
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        //获取锁的名称
        String lockKey = redisLock.key();
        if (!StringUtils.hasLength(lockKey)) {
            //如果注解里没有设置锁的名称,默认使用方法的名称
            lockKey = method.getName();
        }
        //获取到锁的标识
        boolean flag = true;
        int retryCount = redisLock.retry();
        do {
            if (!flag && retryCount > 0) {
                Thread.sleep(1000L);
                retryCount--;
            }
            flag = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", redisLock.expired(), TimeUnit.SECONDS);
            if (flag) {
                //获取到锁结束循环
                log.info("# [BEGIN]获取到分布式锁,key:{}", lockKey);
                break;
            }
            //根据配置的重试次数,执行n次获取锁的方法,默认不重试
        } while (retryCount > 0);

        //result为连接点的返回结果
        Object result = null;
        if (flag) {
            try {
                result = proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                /*异常通知方法*/
                log.error("异常通知方法>目标方法名{},异常为：{}", method.getName(), e);
            } finally {
                stringRedisTemplate.delete(lockKey);
            }
            return result;
        }
        log.error("执行:{} 未获取锁,重试次数:{}", method.getName(), redisLock.retry());
        return null;
    }

    /**
     * 根据切入点获取执行的方法
     */
    private Method currentMethod(JoinPoint joinPoint) {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        //获取目标类的所有方法，找到当前要执行的方法
        Method[] methods = targetClass.getMethods();
        Method resultMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }
}
