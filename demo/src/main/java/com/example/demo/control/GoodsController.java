package com.example.demo.control;

import com.example.demo.Response;
import com.example.demo.aop.CacheEvict;
import com.example.demo.aop.CacheUpdate;
import com.example.demo.aop.CacheablePage;
import com.example.demo.pojo.dto.GoodsDTO;
import com.example.demo.pojo.po.Goods;
import com.example.demo.service.GoodsService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    /*
     * 增加数据
     * 清除列表缓存（确保下次查询更新）
     */
    @PostMapping("/add")
    @CacheUpdate(keyPrefix = "goods:goodsId",params = {"goodsId"})
    @CacheEvict(keyPrefix = "goods:goodsId",params = {"goodsId"})
    public Response<Goods> add(@Validated @RequestBody GoodsDTO goods) {
        return Response.success(goodsService.add(goods));
    }

    /*
     * 查询数据
     * 先从缓存获取
     * 缓存中没有数据，从数据库中获取数据
     * 存入缓存中
     */
    @GetMapping("/get/{goodsId}")
    @CacheablePage(keyPrefix = "goods:goodsId" ,params = {"goodsId"},timeout = 10)
    public Response<Goods> get(@PathVariable Integer goodsId) {
        return Response.success(goodsService.get(goodsId));
    }

    /*
     * 修改数据
     * 更新缓存
     * 清除列表缓存
     */
    @PutMapping("/put")
    @CacheUpdate(keyPrefix = "goods:goodsId",params = {"goodsId}"})
    @CacheEvict(keyPrefix = "goods:list",allEntries = true)
    public Response<Goods> put(@Validated @RequestBody GoodsDTO goods) {
        return Response.success(goodsService.put(goods));
    }

    /*
     * 删除数据
     * 删除缓存
     */
    @DeleteMapping("/del/{goodsId}")
    @CacheEvict(keyPrefix = "goods:goodsId",params = {"goodsId"})
    @CacheEvict(keyPrefix = "goods:list",allEntries = true)
    public Response delete(@PathVariable Integer goodsId) {
        goodsService.delete(goodsId);
        return Response.success();
    }

    /*
     * 按类型分页查询
     */
    @GetMapping("/list/{type}")
    @CacheablePage(keyPrefix = "goods:list", params = {"type"}, timeout = 10, cacheNull = true)
    public Response listByGoodsType(
            @PathVariable @NotBlank String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Response.success(goodsService.listByType(type, PageRequest.of(page - 1, size)));
    }
}

