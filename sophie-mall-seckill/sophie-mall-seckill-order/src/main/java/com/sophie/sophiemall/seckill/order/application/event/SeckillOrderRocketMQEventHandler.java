package com.sophie.sophiemall.seckill.order.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.order.domain.event.SeckillOrderEvent;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER)
public class SeckillOrderRocketMQEventHandler implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(SeckillOrderRocketMQEventHandler.class);

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|orderEvent|接收订单事件为空");
            return ConsumeResult.FAILURE;
        }
        SeckillOrderEvent seckillOrderEvent = this.getEventMessage(message);
        if (seckillOrderEvent.getId() == null){
            logger.info("rocketmq|orderEvent|订单参数错误");
        }
        logger.info("rocketmq|orderEvent|接收订单事件|{}", JSON.toJSON(seckillOrderEvent));
        return ConsumeResult.SUCCESS;
    }

    private SeckillOrderEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillOrderEvent.class);
    }
}
