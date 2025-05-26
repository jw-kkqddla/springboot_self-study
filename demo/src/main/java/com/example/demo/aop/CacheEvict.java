package com.example.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CacheEvict.List.class)
public @interface CacheEvict {
    String keyPrefix();
    String[] params() default {};
    boolean allEntries() default false;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        CacheEvict[] value();
    }
}
