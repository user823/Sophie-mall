package com.sophie.sophiemall.seckill.reservation;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class SeckillReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillReservationApplication.class, args);
    }
}
