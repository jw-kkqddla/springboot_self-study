package com.example.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheablePage {
    String keyPrefix();
    String[] params() default {};
    long timeout() default 30;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    boolean cacheNull() default false;
}
