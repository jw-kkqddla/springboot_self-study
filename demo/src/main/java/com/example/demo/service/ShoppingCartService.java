package com.example.demo.service;

import com.example.demo.pojo.dto.CartDTO;
import com.example.demo.pojo.po.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShoppingCartService {

    boolean addShoppingCart(CartDTO cartDTO);

    List<Cart> ShoppingCartlist();

    void deleteShoppingCart();
}

