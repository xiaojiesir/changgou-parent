package com.changgou.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import entity.Result;
import entity.StatusCode;
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

    @GetMapping(value = "/get/hystrix/ok/{id}")
    public Result getHystrixOk(@PathVariable("id") Long id) {

        return feign.getHystrixOk(id);
    }

    @GetMapping(value = "/get/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "getGoodsTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000")
    })
    public Result getGoodsTimeout(@PathVariable("id") Long id) {
        return feign.getHystrixTimeout(id);
    }

    public Result getGoodsTimeoutHandler(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, "Order=====timeoutHandler:" + Thread.currentThread().getName());
    }
}
