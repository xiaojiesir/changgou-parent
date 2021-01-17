package com.changgou.goods.controller;

import cn.hutool.core.thread.ThreadUtil;
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

    @GetMapping(value = "/get/feign/timeout")
    public Result getTimeOut() {

        ThreadUtil.sleep(3000);
        return new Result(true, StatusCode.OK, port);
    }
}
