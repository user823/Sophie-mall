package com.sophie.sophiemall.seckill.activity;

import com.sophie.sophiemall.seckill.activity.application.builder.SeckillActivityBuilder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class SeckillActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillActivityBuilder.class, args);
    }
}
