package com.changgou.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class ConsumerController {


    @Resource
    private RestTemplate restTemplate;


    @Value("${service-url.nacos-user-service}")
    private String serverUrl;

    @GetMapping(value = "/get/nacos/{id}")
    public Result getGoods(@PathVariable("id") Long id) {

        return restTemplate.getForObject(serverUrl + "/goods/get/nacos/" + id, Result.class);
    }

    @GetMapping(value = "/consumer/fallBack/{id}")
    //@SentinelResource(value = "fallBack")
    //@SentinelResource(value = "fallBack",fallback = "handlerFallBack")
    //@SentinelResource(value = "fallBack",blockHandler = "blockHandlerFallBack")
    @SentinelResource(value = "fallBack", fallback = "handlerFallBack",  blockHandler = "blockHandlerFallBack", exceptionsToIgnore = {IllegalArgumentException.class})
    public String fallBack(@PathVariable("id") Long id) {
        Result result = restTemplate.getForObject(serverUrl + "/goods/get/nacos/" + id, Result.class);
        if (id == 4) {
            throw new IllegalArgumentException("非法的参数,请重试");
        } else if (result.getData() == null) {
            throw new NullPointerException("空指针异常");
        }
        return (String) result.getData();
    }

    public String handlerFallBack(@PathVariable("id") Long id, Throwable e) {
        return "444" + e.getMessage();
    }

    public String blockHandlerFallBack(@PathVariable("id") Long id, Throwable e) {
        return "555" + e.getMessage();
    }
}
