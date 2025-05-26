package com.example.demo.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    // 缓存对象
    public <T> void setObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 获取对象
    public <T> T getObject(final String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    // 删除缓存
    public Boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    // 设置过期时间
    public Boolean expire(final String key, final Long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    //goods:list全部删除
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

