package com.sophie.sophiemall.seckill.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SeckillStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillStockApplication.class, args);
    }
}
