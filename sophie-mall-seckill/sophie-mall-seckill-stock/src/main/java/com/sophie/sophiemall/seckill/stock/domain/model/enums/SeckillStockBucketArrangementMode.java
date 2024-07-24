package com.sophie.sophiemall.seckill.stock.domain.model.enums;

public enum SeckillStockBucketArrangementMode {
    TOTAL(1),        //按总量模式编排
    INCREMENTAL(2);  //按增量模式编排

    private final Integer mode;

    SeckillStockBucketArrangementMode(Integer mode) {
        this.mode = mode;
    }

    public static boolean isTotalArrangementMode(Integer mode) {
        return TOTAL.mode.equals(mode);
    }

    public static boolean isIncrementalArrangementMode(Integer mode) {
        return INCREMENTAL.mode.equals(mode);
    }

    public int getMode() {
        return mode;
    }
}
