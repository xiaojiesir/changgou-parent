package com.changgou.tk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GoodApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodApplication.class, args);
    }
}
