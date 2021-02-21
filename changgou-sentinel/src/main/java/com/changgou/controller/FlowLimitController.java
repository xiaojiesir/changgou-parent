package com.changgou.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.changgou.handler.MyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FlowLimitController {
    @GetMapping("/testA")
    public String testA() {
        return "----testA";
    }

    @GetMapping("/testB")
    public String testB() {
        log.info(Thread.currentThread().getName() + "\t" + "----testB");
        return "----testB";
    }

    @GetMapping("/testC")
    public String testC() {
        int a = 10 / 0;
        log.info(Thread.currentThread().getName() + "\t" + "----testC");
        return "----testC";
    }

    @GetMapping("/testD")
    public String testD() {
        ThreadUtil.sleep(1000);
        log.info(Thread.currentThread().getName() + "\t" + "----testD");
        return "----testD";
    }

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey", blockHandler = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1", required = false) String p1,
                             @RequestParam(value = "p2", required = false) String p2) {
        return "----testHotKey";
    }

    public String deal_testHotKey(String p1, String p2, BlockException e) {
        return "----deal_testHotKey";
    }

    @GetMapping("/testBlockHandler")
    @SentinelResource(value = "testBlockHandler",
            blockHandlerClass = MyHandler.class,
            blockHandler = "handlerException")
    public String testBlockHandler() {
        return "----testBlockHandler,success";
    }
}
