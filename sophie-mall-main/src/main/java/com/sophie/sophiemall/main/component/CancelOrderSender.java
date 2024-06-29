package com.sophie.sophiemall.main.component;


import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

/**
 * 取消订单消息的发出者
 */
@Component
public class CancelOrderSender {
    private static Logger LOGGER =LoggerFactory.getLogger(CancelOrderSender.class);
    @Autowired
    private RocketMQClientTemplate rocketMQClientTemplate;
    @Value("${rocketmq.topic}")
    private String topic;

    public void sendMessage(Long orderId,final long delayTimes){
        //给延迟队列发送消息
        rocketMQClientTemplate.syncSendDelayMessage(topic, orderId, Duration.ofSeconds(delayTimes));
        LOGGER.info("send orderId:{}",orderId);
    }
}
