package com.sophie.sophiemall.seckill.goods.application.message;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.goods.application.service.SeckillGoodsService;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_TX_MSG)
public class GoodsTxMessageListener implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(GoodsTxMessageListener.class);
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        if (StrUtil.isEmpty(message)){
            return ConsumeResult.FAILURE;
        }
        logger.info("秒杀商品微服务开始消费事务消息:{}", message);
        TxMessage txMessage = this.getTxMessage(message);
        //如果协调的异常信息字段为false，订单微服务没有抛出异常，则处理库存信息
        if (BooleanUtil.isFalse(txMessage.getException())){
            seckillGoodsService.updateAvailableStock(txMessage);
        }
        return ConsumeResult.SUCCESS;
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
}
