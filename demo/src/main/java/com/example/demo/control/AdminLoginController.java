package com.example.demo.control;

import com.example.demo.JwtUtil;
import com.example.demo.Response;
import com.example.demo.pojo.dto.AdminDTO;
import com.example.demo.pojo.po.Admin;
import com.example.demo.service.AdminLogin;
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
@RequestMapping("/admin")
public class AdminLoginController {
    @Autowired
    AdminLogin adminLogin;

    /*
     * 以id和name属性确认token的持有者
     */
    @PostMapping("/login")
    public Response login(@Validated @RequestBody AdminDTO admin) {
        log.info("管理员登录:{}", admin);
        Admin e = adminLogin.login(admin);
        if (e != null) {
            Map<String, Object> Claims = new HashMap<>();
            Claims.put("adminId", e.getAdminId());
            Claims.put("adminName", e.getAdminName());
            //Claims.put("password", e.getPassword());
            String jwt = JwtUtil.generateJwt(Claims);
            return Response.success(jwt);
        }
        return Response.error("adminId_null");
    }
}

