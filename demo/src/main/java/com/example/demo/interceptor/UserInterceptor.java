package com.example.demo.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.JwtUtil;
import com.example.demo.Response;
import com.example.demo.context.BaseContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        if(url.contains("login")||url.contains("register")) {
            return true;
        }
        String jwt = request.getHeader("token");
        if(StringUtils.hasLength(jwt)==false) {
            log.info("token为空");
            Response error = Response.error("Not_login");
            String notlogin = JSONObject.toJSONString(error);
            response.getWriter().write(notlogin);
            return false;
        }
        try{
            log.info(jwt);
            Claims claims = JwtUtil.parseJwt(jwt);
            log.info("解析后的 JWT 负载中的 claims: {}", claims);
            Object obj = claims.get("userId");
            if (obj == null) {
                log.info("JWT中不包含userId字段");
                Response error = Response.error("Not_user");
                String notlogin = JSONObject.toJSONString(error);
                response.getWriter().write(notlogin);
                return false;
            }
            String object = obj.toString();
            Integer userid = Integer.parseInt(object);
            BaseContext.setCurrentId(userid);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            log.info("解析失败");
            Response error = Response.error("Not_login");
            String notlogin = JSONObject.toJSONString(error);
            response.getWriter().write(notlogin);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

