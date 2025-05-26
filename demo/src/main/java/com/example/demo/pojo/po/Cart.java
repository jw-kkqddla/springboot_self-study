package com.example.demo.pojo.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "userId")
    private Integer userId;
    @Column(name = "username")
    private String userName;
    @Column(name = "goods_id")
    private Integer goodsId;
    @Column(name = "goodsname")
    private String goodsName;
    @Column(name = "num")
    private int num;
    @Column(name = "price")
    private Double price;
    private LocalDateTime createTime;
}


