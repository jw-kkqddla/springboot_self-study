package com.example.demo.config;

import com.example.demo.interceptor.AdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminConfig implements WebMvcConfigurer {
    @Autowired
    AdminInterceptor adminInterceptor;

    /*
     * 拦截以admin开头的索引请求
     * 登录请求无需拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");
    }
}