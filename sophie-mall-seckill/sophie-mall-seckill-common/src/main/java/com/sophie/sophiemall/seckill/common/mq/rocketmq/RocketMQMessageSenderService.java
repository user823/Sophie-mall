package com.sophie.sophiemall.seckill.common.mq.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.model.message.TopicMessage;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.common.mq.TransactionExecutor;
import com.sophie.sophiemall.seckill.common.mq.TransactionResult;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;
import org.apache.rocketmq.client.common.Pair;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
public class RocketMQMessageSenderService implements MessageSenderService {

    @Autowired
    private RocketMQClientTemplate rocketMQTemplate;

    @Override
    public boolean send(TopicMessage message) {
        try{
            rocketMQTemplate.syncSendNormalMessage(message.getDestination(), this.getMessage(message));
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResult sendMessageInTransaction(TxMessage message, TransactionExecutor executor) {
        TransactionResult result = null;
        Pair<SendReceipt, Transaction> pair;
        try {
            pair = rocketMQTemplate.sendTransactionMessage(message.getDestination(), this.getMessage(message));
            result = executor.executeLocalTransaction(message);
            // 执行本地事务成功
            if (result.getStatus() == TransactionResult.TransactionStatus.COMMIT) {
                pair.getTransaction().commit();
            } else {
                pair.getTransaction().rollback();
            }
        } catch (Exception e) {
            return new TransactionResult(TransactionResult.TransactionStatus.ROLLBACK);
        }
        return result;
    }


    //构建ROcketMQ发送的消息
    private Message<String> getMessage(TopicMessage message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.MSG_KEY, message);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }
}
