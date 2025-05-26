package com.example.demo.aop;

import com.example.demo.cache.RedisCache;
import com.example.demo.pojo.dto.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Aspect
public class CacheUpdateAspect {
    @Autowired
    RedisCache redisCache;

    @Around("@annotation(com.example.demo.aop.CacheUpdate)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        //执行原方法
        Object result = joinPoint.proceed();
        //异步更新缓存
        updateCacheAsync(joinPoint, result, cacheUpdate);
        return result;
    }

    private void updateCacheAsync(ProceedingJoinPoint joinPoint, Object result, CacheUpdate cacheUpdate) {
        CompletableFuture.runAsync(() -> {
            try {
                String key = generateCacheKey(cacheUpdate.keyPrefix(), joinPoint.getArgs());

                if (result != null) {
                    // 特殊处理 Page 类型
                    if (result instanceof Page) {
                        Page<?> page = (Page<?>) result;
                        PageDTO<?> pageDTO = new PageDTO<>(
                                page.getContent(),
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages()
                        );
                        redisCache.setObject(key, pageDTO);
                        redisCache.expire(key, cacheUpdate.timeout(), cacheUpdate.timeUnit());
                    } else {
                        redisCache.setObject(key, result);
                        redisCache.expire(key, cacheUpdate.timeout(), cacheUpdate.timeUnit());
                    }
                    log.info("更新缓存成功: {}", key);
                } else if (cacheUpdate.cacheNull()) {
                    redisCache.setObject(key, null);
                    redisCache.expire(key, cacheUpdate.timeout(), cacheUpdate.timeUnit());
                }
            } catch (Exception e) {
                log.error("更新缓存失败: {}", e.getMessage());
            }
        });
    }

    private String generateCacheKey(String keyPrefix, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder(keyPrefix);
        for (Object arg : args) {
            keyBuilder.append(":").append(arg.toString());
        }
        return keyBuilder.toString();
    }
}

