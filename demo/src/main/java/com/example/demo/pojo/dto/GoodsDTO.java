package com.example.demo.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoodsDTO {

    private Integer goodsId;
    @NotBlank(message = "不能为空")
    private String type;
    @NotBlank(message = "不能为空")
    private String goodsName;
    @NotNull(message = "不能为空")
    private Double price;
    private int inventory;
    private int salenum;
}


