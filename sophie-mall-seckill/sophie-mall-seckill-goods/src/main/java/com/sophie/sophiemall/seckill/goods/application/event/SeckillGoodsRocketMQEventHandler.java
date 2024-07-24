package com.sophie.sophiemall.seckill.goods.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.sophie.sophiemall.seckill.goods.application.cache.SeckillGoodsListCacheService;
import com.sophie.sophiemall.seckill.goods.domain.event.SeckillGoodsEvent;
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
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS)
public class SeckillGoodsRocketMQEventHandler implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsRocketMQEventHandler.class);
    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|goodsEvent|接收秒杀品事件参数错误" );
            return ConsumeResult.FAILURE;
        }
        SeckillGoodsEvent seckillGoodsEvent = this.getEventMessage(message);
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
        return ConsumeResult.SUCCESS;
    }

    private SeckillGoodsEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillGoodsEvent.class);
    }
}