package com.changgou.goods.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController {
    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/get")
    public Result getGoods() {

        return new Result(true, StatusCode.OK, port);
    }
}
