package com.changgou.tk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    public static final String URL = "http://goods-tk";
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    @GetMapping(value = "/order/tk")
    public String getServerPort() {
        return restTemplate.getForObject(URL+"/goods/tk",String.class);

    }
}
