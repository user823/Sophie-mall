package com.sophie.sophiemall.seckill.common.mq;

import com.sophie.sophiemall.seckill.common.model.message.TxMessage;

public interface TransactionExecutor {
    TransactionResult executeLocalTransaction(TxMessage message);
    TransactionResult checkLocalTransaction(TxMessage message);
}
