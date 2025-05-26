package com.example.demo.pojo.po;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "goods")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    private Integer goodsId;
    @Column(name = "type")
    private String type;
    @Column(name = "goodsname")
    private String goodsName;
    @Column(name = "price")
    private Double price;
    @Column(name = "inventory")
    private int inventory;
    @Column(name = "salenum")
    private int salenum;

}

