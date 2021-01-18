package com.changgou.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
@CrossOrigin
@DefaultProperties(defaultFallback = "getTimeoutGlobalHandler")
public class OrderController {
    @Resource
    private com.changgou.order.feign.HystrixFeign HystrixFeign;

    @GetMapping(value = "/get/{id}")
    public Result getGoods(@PathVariable("id") Long id) {
        return HystrixFeign.getGoods(id);

    }

    @GetMapping(value = "/get/feign/timeout")
    public Result getTimeOut() {
        //openfeign-ribbon,客户端一般默认等待一秒钟
        return HystrixFeign.getTimeOut();
    }

    @GetMapping(value = "/get/hystrix/ok/{id}")
    public Result getHystrixOk(@PathVariable("id") Long id) {

        return HystrixFeign.getHystrixOk(id);
    }

    /**
     * Hystrix单独处理方法
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/get/hystrix/timeout/{id}")
   /* @HystrixCommand(fallbackMethod = "getGoodsTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000")
    })*/
    @HystrixCommand
    public Result getGoodsTimeout(@PathVariable("id") Long id) {
        int a = 10 / 0;

        return HystrixFeign.getHystrixTimeout(id);
    }

    public Result getGoodsTimeoutHandler(@PathVariable("id") Long id) {

        return new Result(true, StatusCode.OK, "Order=====timeoutHandler:" + Thread.currentThread().getName());
    }

    /**
     * Hystrix全局处理方法
     *
     * @return
     */
    public Result getTimeoutGlobalHandler() {

        return new Result(true, StatusCode.OK, "Order=====TimeoutGlobalHandler:" + Thread.currentThread().getName());
    }
}
