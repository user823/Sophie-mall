package com.sophie.sophiemall.seckill.activity.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.activity.application.cache.SeckillActivityCacheService;
import com.sophie.sophiemall.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.sophie.sophiemall.seckill.activity.domain.event.SeckillActivityEvent;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
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
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ACTIVITY_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY)
public class SeckillActivityRocketMQEventHandler implements RocketMQListener {

    private final Logger logger = LoggerFactory.getLogger(SeckillActivityRocketMQEventHandler.class);
    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;
    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|activityEvent|事件参数错误" );
            return ConsumeResult.FAILURE;
        }
        SeckillActivityEvent seckillActivityEvent = this.getEventMessage(message);
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus(), false);
        return ConsumeResult.SUCCESS;
    }

    private SeckillActivityEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillActivityEvent.class);
    }
}