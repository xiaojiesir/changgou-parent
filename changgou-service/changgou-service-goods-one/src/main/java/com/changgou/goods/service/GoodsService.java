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
}
