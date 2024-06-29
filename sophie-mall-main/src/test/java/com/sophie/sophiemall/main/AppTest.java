package com.sophie.sophiemall.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest
{
    @Autowired
    RocketMQClientTemplate rmqTemplate;

    @org.junit.jupiter.api.Test
    public void testConnection() {
        System.out.println("hello");
    }
}

