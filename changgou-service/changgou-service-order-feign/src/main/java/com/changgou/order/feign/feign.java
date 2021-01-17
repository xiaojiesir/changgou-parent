package com.changgou.order.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "ribbon-goods")
public interface feign {
    @GetMapping(value = "/goods/get/{id}")
    public Result getGoods(@PathVariable("id") Long id);

    @GetMapping(value = "/goods/get/feign/timeout")
    public Result getTimeOut();

    @GetMapping(value = "/goods/get/hystrix/ok/{id}")
    public Result getHystrixOk(@PathVariable("id") Long id);

    @GetMapping(value = "/goods/get/hystrix/timeout/{id}")
    public Result getHystrixTimeout(@PathVariable("id") Long id);
}
