package com.example.demo.control;

import com.example.demo.Response;
import com.example.demo.aop.CacheablePage;
import com.example.demo.service.GoodsService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/goods")
public class CustomerController {
    @Autowired
    GoodsService goodsService;

    /*
     * 查询数据
     * 先从缓存获取
     * 缓存中没有数据，从数据库中获取数据
     * 存入缓存中
     */
    @GetMapping("/get/{goodsId}")
    @CacheablePage(keyPrefix = "goods:goodsId", timeout = 10)
    public Response get(@PathVariable Integer goodsId) {
        return Response.success(goodsService.get(goodsId));
    }

    /*
     * 按类型分页查询
     */
    @GetMapping("/list/{type}")
    @CacheablePage(keyPrefix = "goods:type", timeout = 10, cacheNull = true)
    public Response listByGoodsType(
            @PathVariable @NotBlank String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Response.success(goodsService.listByType(type, PageRequest.of(page - 1, size)));
    }
}

