package com.changgou.controller;

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
    public Result getOrder(@PathVariable("id") Long id) {

        return restTemplate.getForObject(serverUrl + "/goods/get/nacos/" + id, Result.class);
    }
}
