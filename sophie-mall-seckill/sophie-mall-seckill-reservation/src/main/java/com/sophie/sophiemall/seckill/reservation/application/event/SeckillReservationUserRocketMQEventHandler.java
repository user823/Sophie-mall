package com.sophie.sophiemall.seckill.reservation.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillReservationUserStatus;
import com.sophie.sophiemall.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import com.sophie.sophiemall.seckill.reservation.application.cache.SeckillReservationUserCacheService;
import com.sophie.sophiemall.seckill.reservation.domain.event.SeckillReservationUserEvent;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_RESERVATION_USER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_RESERVATION_USER)
public class SeckillReservationUserRocketMQEventHandler implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationUserRocketMQEventHandler.class);
    @Autowired
    private SeckillReservationUserCacheService seckillReservationUserCacheService;
    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件参数错误");
            return ConsumeResult.FAILURE;
        }
        SeckillReservationUserEvent seckillReservationUserEvent = this.getEventMessage(message);
        //获取的数据为空
        if (seckillReservationUserEvent == null || seckillReservationUserEvent.getId() == null || seckillReservationUserEvent.getGoodsId() == null){
            logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件参数错误");
            return ConsumeResult.FAILURE;
        }
        logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件解析后的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
        if (seckillReservationUserEvent.getStatus() != null && SeckillReservationUserStatus.isDeleted(seckillReservationUserEvent.getStatus())){
            logger.info("rocketmq|reservationUserEvent|删除缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.deleteSeckillReservationUserFromCache(seckillReservationUserEvent);
        }else{
            logger.info("rocketmq|reservationUserEvent|更新缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.tryUpdateSeckillReservationUserCacheByUserIdAndGoodsId(seckillReservationUserEvent.getId(), seckillReservationUserEvent.getGoodsId(), false);
            seckillReservationUserCacheService.tryUpdateGoodsListCacheByUserId(seckillReservationUserEvent.getId(), false);
            seckillReservationUserCacheService.tryUpdatetUserListCacheByGoodsId(seckillReservationUserEvent.getGoodsId(), false);
        }
        seckillReservationConfigCacheService.updateSeckillReservationConfigCurrentUserCount(seckillReservationUserEvent.getGoodsId(), seckillReservationUserEvent.getStatus(), 0L);
        return ConsumeResult.SUCCESS;
    }

    private SeckillReservationUserEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillReservationUserEvent.class);
    }
}
