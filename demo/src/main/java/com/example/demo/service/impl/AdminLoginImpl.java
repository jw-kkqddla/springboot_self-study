package com.example.demo.service.impl;

import com.example.demo.mapper.AdminLoginMapper;
import com.example.demo.pojo.dto.AdminDTO;
import com.example.demo.pojo.po.Admin;
import com.example.demo.service.AdminLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginImpl implements AdminLogin {
    @Autowired
    AdminLoginMapper adminLoginMapper;

    @Override
    public Admin login(AdminDTO admin) {
        Admin adminlogin = adminLoginMapper.login(admin);
        return adminlogin;
    }
}
