package com.sophie.sophiemall.seckill.stock.application.message;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSONObject;
import cn.hutool.core.util.StrUtil;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.stock.application.service.SeckillStockBucketService;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_STOCK_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_BUCKET_TX_MSG)
public class StockTxMessageListener implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(StockTxMessageListener.class);
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            return ConsumeResult.FAILURE;
        }
        logger.info("秒杀库存微服务开始消费事务消息:{}", message);
        TxMessage txMessage = this.getTxMessage(message);
        //如果表示异常信息字段为false，订单微服务没有抛出异常，则处理库存信息
        if (BooleanUtil.isFalse(txMessage.getException())){
            seckillStockBucketService.decreaseStock(txMessage);
        }
        return ConsumeResult.SUCCESS;
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
}
