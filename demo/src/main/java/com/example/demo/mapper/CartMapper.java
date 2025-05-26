package com.example.demo.mapper;

import com.example.demo.pojo.po.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    @Select("select * from cart where userId = #{userId} and goods_id = #{goodsId}")
    List<Cart> list(Cart cart);
    @Insert("insert into cart(id,userId,username,goods_id,goodsname,num,price,createtime)" +
            "values (#{id},#{userId},#{userName},#{goodsId},#{goodsName},#{num},#{price},#{createTime})")
    void insert(Cart cart);
    @Delete("delete from cart where userId = #{userId}")
    void delete(Integer userId);
    @Update("update cart set num = #{num} where userId = #{userId}")
    void update(Cart cart);
}

