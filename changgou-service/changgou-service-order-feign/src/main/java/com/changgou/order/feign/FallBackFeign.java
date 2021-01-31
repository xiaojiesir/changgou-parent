package com.changgou.order.feign;

import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class FallBackFeign implements HystrixFeign {
    @Override
    public Result getGoods(Long id) {
        return new Result(true, StatusCode.OK, "FallBackFeign:getGoods========" + Thread.currentThread().getName());
    }

    @Override
    public Result getTimeOut() {
        return new Result(true, StatusCode.OK, "FallBackFeign:getTimeOut========" + Thread.currentThread().getName());
    }

    @Override
    public Result getHystrixOk(Long id) {
        return new Result(true, StatusCode.OK, "FallBackFeign:getHystrixOk========" + Thread.currentThread().getName());
    }

    @Override
    public Result getHystrixTimeout(Long id) {
        return new Result(true, StatusCode.OK, "FallBackFeign:getHystrixTimeout========" + Thread.currentThread().getName());
    }

    @Override
    public Result getzipkin() {
        return null;
    }
}
