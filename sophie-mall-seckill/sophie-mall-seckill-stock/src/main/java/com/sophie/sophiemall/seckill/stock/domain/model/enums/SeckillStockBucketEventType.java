package com.sophie.sophiemall.seckill.stock.domain.model.enums;

public enum SeckillStockBucketEventType {

    DISABLED(0),
    ENABLED(1),
    ARRANGED(2);

    public Integer getCode() {
        return code;
    }

    private final Integer code;

    SeckillStockBucketEventType(Integer code) {
        this.code = code;
    }
}
