package com.sophie.sophiemall.main.component;

import com.sophie.sophiemall.main.service.OmsMainOrderService;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 取消订单消息的处理者
 */
@Service
@RocketMQMessageListener(endpoints="${rocketmq.endpoints}", topic="${rocketmq.topic}", consumerGroup="${rocketmq.consumer-group}", accessKey="${rocketmq.access-key}", secretKey="${rocketmq.secret-key}", tag = "${rocketmq.tag}")
public class CancelOrderReceiver implements RocketMQListener {
    private static Logger LOGGER =LoggerFactory.getLogger(CancelOrderReceiver.class);
    @Autowired
    private OmsMainOrderService mainOrderService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        Long orderId = messageView.getBody().asLongBuffer().get();
        mainOrderService.cancelOrder(orderId);
        LOGGER.info("process orderId:{}",orderId);
        return ConsumeResult.SUCCESS;
    }
}
