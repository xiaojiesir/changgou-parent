package com.changgou.order.controller;

import entity.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    @Resource
    private com.changgou.order.feign.feign feign;

    @GetMapping(value = "/get/{id}")
    public Result getGoods(@PathVariable("id") Long id) {
        return feign.getGoods(id);

    }

    @GetMapping(value = "/get/feign/timeout")
    public Result getTimeOut() {
        //openfeign-ribbon,客户端一般默认等待一秒钟
        return feign.getTimeOut();
    }
}
