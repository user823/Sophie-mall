package com.sophie.sophiemall.seckill.order;

import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class SeckillOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillOrderApplication.class, args);
    }
}
