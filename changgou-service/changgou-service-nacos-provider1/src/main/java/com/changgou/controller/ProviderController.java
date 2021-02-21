package com.changgou.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
@CrossOrigin
public class ProviderController {
    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/get/nacos/{id}")
    public Result getGoods(@PathVariable("id") Long id) {
        if (id < 4) {
            return new Result(true, StatusCode.OK, "操作成功", "port:" + port + ";id=" + id);
        } else {
            return new Result(true, StatusCode.OK, "操作失败", null);
        }

    }
}
