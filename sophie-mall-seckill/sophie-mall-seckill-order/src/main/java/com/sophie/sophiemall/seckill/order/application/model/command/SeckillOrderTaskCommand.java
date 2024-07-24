package com.sophie.sophiemall.seckill.order.application.model.command;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SeckillOrderTaskCommand {

    /**
     * 订单任务id
     */
    private String orderTaskId;

    /**
     * 商品id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}