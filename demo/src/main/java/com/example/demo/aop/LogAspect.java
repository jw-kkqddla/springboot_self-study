package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class LogAspect {
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    // 切入点：所有 Controller 方法
    @Pointcut("execution(* com.example.demo.control.*.*(..))")
    public void controllerPointcut() {}

    // 切入点：所有 Service 方法
    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void servicePointcut() {}

    // 前置通知：记录请求信息
    @Before("controllerPointcut()")
    public void beforeController(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());

        // 获取 HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 记录请求信息
            log.info("请求开始");
            log.info("请求路径: {}", request.getRequestURI());
            log.info("请求方法: {}", request.getMethod());
            log.info("请求IP: {}", request.getRemoteAddr());

            // 获取方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            log.info("请求方法: {}.{}", joinPoint.getTarget().getClass().getName(), method.getName());

            // 获取请求参数
            Map<String, Object> paramMap = new HashMap<>();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            if (parameterNames != null && parameterNames.length > 0) {
                for (int i = 0; i < parameterNames.length; i++) {
                    paramMap.put(parameterNames[i], args[i]);
                }
            }

            log.info("请求参数: {}", paramMap);
        }
    }

    // 环绕通知：记录方法执行时间和返回结果
    @Around("servicePointcut()")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.info("Service方法执行时间: {}.{} 耗时: {}ms",
                joinPoint.getTarget().getClass().getName(),
                method.getName(),
                end - start);

        return result;
    }

    // 返回通知：记录返回结果
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime.get();
        startTime.remove();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.info("请求结束");
        log.info("请求方法: {}.{}", joinPoint.getTarget().getClass().getName(), method.getName());
        log.info("返回结果: {}", result);
        log.info("请求耗时: {}ms", executeTime);
    }

    // 异常通知：记录异常信息
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.error("请求异常");
        log.error("异常方法: {}.{}", joinPoint.getTarget().getClass().getName(), method.getName());
        log.error("异常信息: {}", e.getMessage(), e);
    }
}