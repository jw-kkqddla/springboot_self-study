package com.example.demo.service.impl;

import com.example.demo.mapper.UserLoginMapper;
import com.example.demo.pojo.dto.UserDTO;
import com.example.demo.pojo.po.User;
import com.example.demo.service.UserLogin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginImpl implements UserLogin {
    @Autowired
    UserLoginMapper userLoginMapper;

    @Override
    public User login(String userName, String password) {
        User user = userLoginMapper.findByUnameAndPassword(userName, password);
        return user;
    }

    @Override
    public User register(UserDTO user) {
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        //当新用户的用户名已存在时
        if (userLoginMapper.findByUname(user.getUserName()) != null) {
            // 无法注册
            return null;
        } else {
            // 插入用户到数据库
            int result = userLoginMapper.insertUser(user);
            if (result > 0) {
                //返回创建好的用户对象
                user.setPassword("");
                return newUser;
            } else {
                return null;
            }
        }
    }
}

