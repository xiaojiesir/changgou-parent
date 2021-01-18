package com.changgou.goods.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoodsService {
    public String getGoodsOk(Long id) {
        return "ok:" + Thread.currentThread().getName();
    }

    @HystrixCommand(fallbackMethod = "getGoodsTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String getGoodsTimeout(Long id) {
        //ThreadUtil.sleep(2000);
        int a = 10 / 0;
        log.info("timeout:" + Thread.currentThread().getName());
        return "timeout:" + Thread.currentThread().getName();
    }

    public String getGoodsTimeoutHandler(Long id) {
        return "goods========timeoutHandler:" + Thread.currentThread().getName();
    }

    //服务熔断
    @HystrixCommand(fallbackMethod = "getGoodsCircuitBreakerHandler", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),//是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),//请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),//失败率达到多少后跳闸
    })
    public String getGoodsCircuitBreaker(Long id) {
        if (id < 0) {
            throw new RuntimeException("id不能小于0");
        }
        log.info("timeout:" + Thread.currentThread().getName());
        return "调用熔断成功" + Thread.currentThread().getName();
    }

    public String getGoodsCircuitBreakerHandler(Long id) {
        return "goods========CircuitBreakerHandler:" + Thread.currentThread().getName();
    }
}
