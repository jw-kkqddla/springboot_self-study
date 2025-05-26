package com.example.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheUpdate {
    String keyPrefix();  // 缓存键前缀
    String[] params() default {};
    long timeout() default 30;  // 缓存超时时间
    TimeUnit timeUnit() default TimeUnit.MINUTES;  // 时间单位
    boolean cacheNull() default false;  // 是否缓存空值
}
