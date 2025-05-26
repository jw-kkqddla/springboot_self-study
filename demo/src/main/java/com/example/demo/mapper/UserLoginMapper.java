package com.example.demo.mapper;

import com.example.demo.pojo.dto.UserDTO;
import com.example.demo.pojo.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserLoginMapper {
    @Select("select * from user where username = #{userName}")
    User findByUname(String userName);
    @Select("select * from user where username = #{userName} and password = #{password}")
    User findByUnameAndPassword(String userName, String password);
    @Insert("insert into user (username, password) values (#{userName}, #{password})")
    int insertUser(UserDTO user);
    @Select("select * from user where userId = #{userId}")
    User findUserById(Integer userId);
}

