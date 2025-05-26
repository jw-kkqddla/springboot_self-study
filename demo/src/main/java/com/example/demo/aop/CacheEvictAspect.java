package com.example.demo.aop;

import com.example.demo.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class CacheEvictAspect {

    @Autowired
    RedisCache redisCache;

    @Around("@annotation(com.example.demo.aop.CacheEvict)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法上的 CacheEvict 注解
        CacheEvict[] cacheEvicts = getCacheEvicts(joinPoint);

        // 执行原方法
        Object result = joinPoint.proceed();

        // 清除所有指定的缓存
        for (CacheEvict cacheEvict : cacheEvicts) {
            try {
                if (cacheEvict.allEntries()) {
                    redisCache.deleteByPattern(cacheEvict.keyPrefix() + "*");
                } else {
                    redisCache.delete(cacheEvict.keyPrefix());
                }
                log.info("缓存已清除: {}", cacheEvict.keyPrefix());
            } catch (Exception e) {
                log.error("清除缓存失败: {}", e.getMessage());
            }
        }

        return result;
    }

    private CacheEvict[] getCacheEvicts(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 检查是否有直接的 CacheEvict 注解
        if (method.isAnnotationPresent(CacheEvict.class)) {
            return new CacheEvict[]{method.getAnnotation(CacheEvict.class)};
        }

        return new CacheEvict[0];
    }
}