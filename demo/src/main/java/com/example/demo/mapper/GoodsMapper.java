package com.example.demo.mapper;

import com.example.demo.pojo.po.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper {
    @Update("update goods set salenum = #{salenum} where goods_id = #{goodsId}")
    void cancel(Goods goods);
    @Select("select * from goods where goods_id = #{goodsId}")
    Goods getById(Integer goodsId);
    @Update("update goods set salenum = #{salenum} where goods_id = #{goodsId}")
    void sell(Goods goods);
}

