package com.example.demo.mapper;

import com.example.demo.pojo.dto.AdminDTO;
import com.example.demo.pojo.po.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminLoginMapper {
    @Select("select * from admin where adminname = #{adminName} and password = #{password}")
    Admin login(AdminDTO admin);
}
