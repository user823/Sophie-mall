package com.sophie.sophiemall.seckill.order.application.message;

import cn.hutool.core.util.StrUtil;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.message.ErrorMessage;
import com.sophie.sophiemall.seckill.order.application.service.SeckillOrderService;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSONObject;

@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_ERROR_MSG)
public class OrderErrorMessageListener implements RocketMQListener {
    private final Logger logger = LoggerFactory.getLogger(OrderErrorMessageListener.class);
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Override
    public ConsumeResult consume(MessageView messageView) {
        String message = messageView.getBody().toString();
        logger.info("onMessage|秒杀订单微服务开始消费消息:{}", message);
        if (StrUtil.isEmpty(message)){
            return ConsumeResult.FAILURE;
        }
        //删除数据库中对应的订单
        seckillOrderService.deleteOrder(this.getErrorMessage(message));
        return ConsumeResult.SUCCESS;
    }

    private ErrorMessage getErrorMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, ErrorMessage.class);
    }
}
