package com.changgou.tk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GoodsController {
    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/goods/tk")
    public String getServerPort() {
        return port + UUID.randomUUID().toString();
    }
}
