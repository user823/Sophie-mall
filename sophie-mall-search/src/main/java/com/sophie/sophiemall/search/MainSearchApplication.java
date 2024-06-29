package com.sophie.sophiemall.search;

import com.sophie.sophiemall.search.service.EsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MainSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainSearchApplication.class, args);
    }
}
