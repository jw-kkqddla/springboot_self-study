package com.example.demo.control;

import com.example.demo.Response;

import com.example.demo.pojo.dto.CartDTO;
import com.example.demo.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shoppingcart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public Response add(@RequestBody CartDTO cartDTO) {
        log.info("add cart: {}", cartDTO);
        if(shoppingCartService.addShoppingCart(cartDTO)) {
            return Response.success();
        }else{
            return Response.error("库存不足！");
        }


    }
    @GetMapping("/list")
    public Response list() {
        log.info("list");
        return Response.success(shoppingCartService.ShoppingCartlist());
    }
    @DeleteMapping("/del")
    public Response delete(){
        log.info("delete");
        shoppingCartService.deleteShoppingCart();
        return Response.success();
    }
}

