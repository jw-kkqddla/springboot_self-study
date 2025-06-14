package com.example.demo.mapper;

import com.example.demo.pojo.po.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRespository extends JpaRepository<Goods, Integer> {
    Page<Goods> findByType(String type, Pageable pageable);
}


