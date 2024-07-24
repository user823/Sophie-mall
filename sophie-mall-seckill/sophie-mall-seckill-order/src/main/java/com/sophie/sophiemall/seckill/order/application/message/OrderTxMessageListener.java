package com.sophie.sophiemall.seckill.order.application.message;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.TransactionResult;
import com.sophie.sophiemall.seckill.order.application.place.SeckillPlaceOrderService;
import org.apache.rocketmq.client.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;
import org.apache.rocketmq.client.core.RocketMQTransactionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RocketMQTransactionListener(/*txProducerGroup = SeckillConstants.TX_ORDER_PRODUCER_GROUP,*/ rocketMQTemplateBeanName = "rocketMQTemplate")
public class OrderTxMessageListener implements RocketMQTransactionChecker {
    private final Logger logger = LoggerFactory.getLogger(OrderTxMessageListener.class);
    @Autowired
    private SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    public TransactionResolution check(MessageView messageView) {
        TransactionResult result = seckillPlaceOrderService.checkLocalTransaction(getTxMessage(messageView));
        if (result.getStatus() == TransactionResult.TransactionStatus.COMMIT) {
            return TransactionResolution.COMMIT;
        }
        return TransactionResolution.ROLLBACK;
    }

    private TxMessage getTxMessage(MessageView messageView) {
        String message = messageView.getBody().toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
}
