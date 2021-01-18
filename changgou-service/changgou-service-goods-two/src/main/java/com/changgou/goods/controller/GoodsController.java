package com.changgou.goods.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController {
    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/get/{id}")
    public Result getGoods(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, port);
    }
}
