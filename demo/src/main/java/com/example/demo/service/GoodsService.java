package com.example.demo.service;

import com.example.demo.pojo.dto.GoodsDTO;
import com.example.demo.pojo.po.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface GoodsService {

    Goods add(GoodsDTO goods);
    Goods get(Integer goodsId);
    Goods put(GoodsDTO goods);
    void delete(Integer goodsId);
    Page<Goods> listByType(String type, Pageable pageable);
}
