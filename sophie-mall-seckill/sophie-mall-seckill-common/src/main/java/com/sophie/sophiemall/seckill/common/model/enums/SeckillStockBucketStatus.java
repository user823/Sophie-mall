package com.sophie.sophiemall.seckill.common.model.enums;

public enum SeckillStockBucketStatus {

    ENABLED(1),
    DISABLED(0);

    private final Integer code;

    SeckillStockBucketStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}