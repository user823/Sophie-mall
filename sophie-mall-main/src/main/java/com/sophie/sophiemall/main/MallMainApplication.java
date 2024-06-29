package com.sophie.sophiemall.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.sophie.sophiemall", "com.sophie.sophiemall.main"})
public class MallMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallMainApplication.class, args);
    }
}
