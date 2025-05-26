package com.example.demo.service;

import com.example.demo.pojo.dto.UserDTO;
import com.example.demo.pojo.po.User;
import org.springframework.stereotype.Service;

@Service
public interface UserLogin {
    User login(String userName, String password);
    User register(UserDTO user);
}

