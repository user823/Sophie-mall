package com.sophie.sophiemall.seckill.common.mq;

import com.sophie.sophiemall.seckill.common.model.message.TopicMessage;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;

public interface MessageSenderService {

    /**
     * 发送消息
     * @param message 发送的消息
     */
    boolean send(TopicMessage message);

    /**
     * 发送事务消息，主要是RocketMQ
     * @param message 事务消息
     * @param executor 其他参数
     * @return 返回事务发送结果
     */
    default TransactionResult sendMessageInTransaction(TxMessage message, TransactionExecutor executor){
        return null;
    }
}
