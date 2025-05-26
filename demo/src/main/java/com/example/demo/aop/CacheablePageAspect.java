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
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class CacheablePageAspect {

    @Autowired
    RedisCache redisCache;

    @Around("@annotation(com.example.demo.aop.CacheablePage)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        /*
         * 获取拦截方法的签名
         * 获取被拦截方法的反射对象
         * 检查方法是否被@CacheablePage注解修饰
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheablePage cacheable = method.getAnnotation(CacheablePage.class);

        /*
         * 生成缓存键
         * 尝试从缓存获取
         * 执行原方法
         * 缓存数据
         */
        String key = generateCacheKey(cacheable.keyPrefix(), joinPoint.getArgs());

        try {
            Object cachedValue = redisCache.getObject(key);
            if (cachedValue != null) {
                log.info("获取缓存成功: {}", key);
                if (cachedValue instanceof PageDTO) {
                    return ((PageDTO<?>) cachedValue).toPage();
                }
                return cachedValue;
            }
        } catch (Exception e) {
            log.error("获取缓存失败: {}", e.getMessage());
        }

        Object result = joinPoint.proceed();
        cacheResultAsync(key, result, cacheable);
        return result;
    }
    //将KeyPrefix与所有方法拼成缓存键
    private String generateCacheKey(String keyPrefix, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder(keyPrefix);
        for (Object arg : args) {
            keyBuilder.append(":").append(arg.toString());
        }
        return keyBuilder.toString();
    }

    private void cacheResultAsync(String key, Object result, CacheablePage cacheable) {
        CompletableFuture.runAsync(() -> {
            try {
                if (result != null) {
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
                        redisCache.expire(key, cacheable.timeout(), cacheable.timeUnit());
                    } else {
                        redisCache.setObject(key, result);
                        redisCache.expire(key, cacheable.timeout(), cacheable.timeUnit());
                    }
                    log.info("缓存成功: {}", key);
                } else if (cacheable.cacheNull()) {
                    redisCache.setObject(key, null);
                    redisCache.expire(key, cacheable.timeout(), TimeUnit.MINUTES);
                }
            } catch (Exception e) {
                log.error("缓存失败: {}", e.getMessage());
            }
        });
    }
}
