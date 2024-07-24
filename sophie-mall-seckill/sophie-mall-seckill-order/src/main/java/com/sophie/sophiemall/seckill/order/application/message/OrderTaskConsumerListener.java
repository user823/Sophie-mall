package com.sophie.sophiemall.seckill.order.application.message;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.order.application.model.task.SeckillOrderTask;
import com.sophie.sophiemall.seckill.order.application.service.SeckillSubmitOrderService;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
@RocketMQMessageListener(consumerGroup = SeckillConstants.SUBMIT_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_ORDER_MSG)
public class OrderTaskConsumerListener implements RocketMQListener {

    private final Logger logger = LoggerFactory.getLogger(OrderTaskConsumerListener.class);

    @Autowired
    private SeckillSubmitOrderService seckillSubmitOrderService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        logger.info("onMessage|秒杀订单微服务接收异步订单任务消息:{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息为空:{}", message);
            return ConsumeResult.FAILURE;
        }
        SeckillOrderTask seckillOrderTask = this.getTaskMessage(message);
        if (seckillOrderTask.isEmpty()){
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息转换成任务对象为空{}", message);
            return ConsumeResult.FAILURE;
        }
        logger.info("onMessage|处理下单任务:{}", seckillOrderTask.getOrderTaskId());
        seckillSubmitOrderService.handlePlaceOrderTask(seckillOrderTask);
        return ConsumeResult.SUCCESS;
    }


    private SeckillOrderTask getTaskMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, SeckillOrderTask.class);
    }
}
