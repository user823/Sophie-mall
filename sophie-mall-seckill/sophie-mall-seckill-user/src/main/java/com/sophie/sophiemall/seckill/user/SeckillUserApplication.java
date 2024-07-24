package com.sophie.sophiemall.seckill.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
public class SeckillUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillUserApplication.class, args);
    }
}
