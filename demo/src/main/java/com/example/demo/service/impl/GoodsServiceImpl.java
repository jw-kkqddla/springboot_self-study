package com.example.demo.service.impl;

import com.example.demo.mapper.GoodsRespository;
import com.example.demo.pojo.dto.GoodsDTO;
import com.example.demo.pojo.po.Goods;
import com.example.demo.service.GoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsRespository goodsRespository;

    @Override
    public Goods add(GoodsDTO goods) {
        Goods goodsadd = new Goods();
        BeanUtils.copyProperties(goods, goodsadd);
        return goodsRespository.save(goodsadd);
    }

    @Override
    public Goods get(Integer goodsId) {
        return goodsRespository.findById(goodsId).orElseThrow(() ->  new IllegalArgumentException("商品不存在"));
    }

    @Override
    public Goods put(GoodsDTO user){
        Goods goodsput = new Goods();
        BeanUtils.copyProperties(user, goodsput);
        return goodsRespository.save(goodsput);
    }

    @Override
    public void delete(Integer goodsId) {
        goodsRespository.deleteById(goodsId);
    }

    @Override
    public Page<Goods> listByType(String type, Pageable pageable) {
        return goodsRespository.findByType(type, pageable);
    }
}

