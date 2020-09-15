package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
//禁止了DataSource的自动加载创建
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableEurekaServer//开启Eureka服务
public class EurekaApplication {
    /**
     * 加载启动类，以启动类为当前springboot的配置标准
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
