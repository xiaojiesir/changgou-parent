package com.changgou.goods.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.changgou.goods.service.GoodsService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/goods")
@CrossOrigin

public class GoodsController {
    @Value("${server.port}")
    private String port;
    @Resource
    private GoodsService goodsService;

    @GetMapping(value = "/get/{id}")
    public Result getGoods(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, port);
    }

    @GetMapping(value = "/get/feign/timeout")
    public Result getTimeOut() {

        ThreadUtil.sleep(3000);
        return new Result(true, StatusCode.OK, port);
    }

    @GetMapping(value = "/get/hystrix/ok/{id}")
    public Result getHystrixOk(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, goodsService.getGoodsOk(id));
    }

    @GetMapping(value = "/get/hystrix/timeout/{id}")
    public Result getHystrixTimeout(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, goodsService.getGoodsTimeout(id));
    }

    @GetMapping(value = "/get/hystrix/circuit/{id}")
    public Result getHystrixCircuit(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, goodsService.getGoodsCircuitBreaker(id));
    }
}
