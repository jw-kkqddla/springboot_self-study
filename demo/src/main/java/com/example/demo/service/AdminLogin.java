package com.example.demo.service;

import com.example.demo.pojo.dto.AdminDTO;
import com.example.demo.pojo.po.Admin;
import org.springframework.stereotype.Service;

@Service
public interface AdminLogin {
    Admin login(AdminDTO admin);
}

