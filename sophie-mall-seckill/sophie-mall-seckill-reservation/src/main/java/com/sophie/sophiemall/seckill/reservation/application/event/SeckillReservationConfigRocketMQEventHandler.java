package com.sophie.sophiemall.seckill.reservation.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import com.sophie.sophiemall.seckill.reservation.domain.event.SeckillReservationConfigEvent;
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
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_RESERVATION_CONFIG_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_RESERVATION_CONFIG)
public class SeckillReservationConfigRocketMQEventHandler implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationConfigRocketMQEventHandler.class);
    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件参数错误");
            return ConsumeResult.FAILURE;
        }
        SeckillReservationConfigEvent seckillReservationConfigEvent = this.getEventMessage(message);
        if (seckillReservationConfigEvent == null){
            logger.info("rocketmq|reservationConfigEvent|解析后的数据为空");
            return ConsumeResult.FAILURE;
        }
        logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件解析后的数据|{}", JSON.toJSONString(seckillReservationConfigEvent));
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigCacheByLock(seckillReservationConfigEvent.getId(), false);
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigListCacheByLock(false);
        return ConsumeResult.SUCCESS;
    }

    private SeckillReservationConfigEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillReservationConfigEvent.class);
    }
}
