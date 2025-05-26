package com.example.demo.control;

import com.example.demo.JwtUtil;
import com.example.demo.Response;
import com.example.demo.pojo.dto.UserDTO;
import com.example.demo.pojo.po.User;
import com.example.demo.service.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    UserLogin userLogin;

    /*
     * 以id和name属性确认token的持有者
     */
    @PostMapping("/login")
    public Response login(@Validated @RequestBody UserDTO user) {
        log.info("用户登录:{}", user);
        User e = userLogin.login(user.getUserName(), user.getPassword());
        //如果登录成功，生成令牌，下发令牌
        if (e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId",e.getUserId());
            claims.put("userName", e.getUserName());
            //claims.put("username", e.getName());
            String jwt = JwtUtil.generateJwt(claims);  //包含员工登录的信息
            return Response.success(jwt);
        }
        //登录失败，返回错误信息
        return Response.error("账户或者密码错误");
    }
    @PostMapping("/register")
    public Response userregister(@Validated @RequestBody UserDTO newuser) {
        log.info("用户注册:{}", newuser);
        User e = userLogin.register(newuser);
        if (e != null) {
            return Response.success();
        }else {
            return Response.error("用户已存在");
        }
    }
}
