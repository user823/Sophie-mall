package com.sophie.sophiemall.seckill.stock.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.stock.application.cache.SeckillStockBucketCacheService;
import com.sophie.sophiemall.seckill.stock.domain.event.SeckillStockBucketEvent;
import com.sophie.sophiemall.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
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
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_STOCK_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_STOCK)
public class SeckillStockRocketMQEventHandler implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(SeckillStockRocketMQEventHandler.class);
    @Autowired
    private SeckillStockBucketCacheService seckillStockBucketCacheService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|stockEvent|接收库存事件为空");
            return ConsumeResult.FAILURE;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = this.getEventMessage(message);
        if (seckillStockBucketEvent.getId() == null){
            logger.info("rocketmq|stockEvent|订单参数错误");
        }
        logger.info("rocketmq|stockEvent|接收订单事件|{}", JSON.toJSON(seckillStockBucketEvent));
        //开启了库存分桶，就更新缓存数据
        if (SeckillStockBucketEventType.ENABLED.getCode().equals(seckillStockBucketEvent.getStatus())){
            seckillStockBucketCacheService.tryUpdateSeckillStockBucketCacheByLock(seckillStockBucketEvent.getId(), false);
        }
        return ConsumeResult.SUCCESS;
    }

    private SeckillStockBucketEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillStockBucketEvent.class);
    }
}
