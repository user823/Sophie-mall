package com.sophie.sophiemall.seckill.common.mq;

public class TransactionResult {
    public enum TransactionStatus {
        COMMIT,
        ROLLBACK
    }

    private TransactionStatus status;

    public TransactionResult(TransactionStatus status) {
        this.status = status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }
}
