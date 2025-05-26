package com.example.demo.service.impl;

import com.example.demo.context.BaseContext;
import com.example.demo.mapper.CartMapper;
import com.example.demo.mapper.GoodsMapper;
import com.example.demo.mapper.UserLoginMapper;
import com.example.demo.pojo.dto.CartDTO;
import com.example.demo.pojo.po.Cart;
import com.example.demo.pojo.po.Goods;
import com.example.demo.pojo.po.User;
import com.example.demo.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartImpl implements ShoppingCartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    UserLoginMapper userLoginMapper;
    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public boolean addShoppingCart(CartDTO cartDTO) {
        Cart cart = new Cart();
        cart.setGoodsId(cartDTO.getGoodsId());
        Goods goods = goodsMapper.getById(cartDTO.getGoodsId());
        if(goods.getInventory()==goods.getSalenum()){
            return false;
        }
        Integer userid = BaseContext.getCurrentId();
        User user = userLoginMapper.findUserById(userid);
        cart.setUserId(userid);
        List<Cart> listcart = cartMapper.list(cart);
        if(listcart != null && listcart.size() > 0) {
            for (Cart existingCart:listcart)
                try{
                    existingCart.setNum(existingCart.getNum()+1);
                    cartMapper.update(existingCart);
                }catch (Exception e){
                    log.error("添加失败，记录id：{}",existingCart.getGoodsId(),e);
                }
        }else{
            cart.setNum(1);
            cart.setGoodsName(goods.getGoodsName());
            cart.setPrice(goods.getPrice());
            cart.setCreateTime(LocalDateTime.now());
            cart.setUserName(user.getUserName());
            cartMapper.insert(cart);
        }
        goods.setSalenum(goods.getSalenum()+1);
        goodsMapper.sell(goods);
        return true;
    }

    public List<Cart> ShoppingCartlist() {
        Integer userid = BaseContext.getCurrentId();
        Cart cart = new Cart();
        cart.setUserId(userid);
        return cartMapper.list(cart);
    }

    @Override
    public void deleteShoppingCart() {
        Integer userid = BaseContext.getCurrentId();
        Cart cart = new Cart();
        cart.setUserId(userid);
        List<Cart> listcart = cartMapper.list(cart);
        if (listcart == null){
            return;
        }
        for(int i=0;i<listcart.size();i++){
            Goods goods = goodsMapper.getById(listcart.get(i).getGoodsId());
            goods.setSalenum(goods.getSalenum()-listcart.get(i).getNum());
            goodsMapper.cancel(goods);
        }
        cartMapper.delete(userid);
    }

}

