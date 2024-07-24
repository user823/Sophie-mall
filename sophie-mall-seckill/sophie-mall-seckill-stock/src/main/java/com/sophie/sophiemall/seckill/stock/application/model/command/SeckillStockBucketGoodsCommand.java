package com.sophie.sophiemall.seckill.stock.application.model.command;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class SeckillStockBucketGoodsCommand implements Serializable {
    private static final long serialVersionUID = -3277417752771378782L;
    //用户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;

    public SeckillStockBucketGoodsCommand() {
    }

    public SeckillStockBucketGoodsCommand(Long userId, Long goodsId) {
        this.userId = userId;
        this.goodsId = goodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public boolean isEmpty(){
        return this.userId == null
                || this.goodsId == null;
    }
}
