package com.sophie.sophiemall.seckill.order.application.model.command;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class SeckillOrderCommand implements Serializable {

    private static final long serialVersionUID = 2150071992328498340L;
    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;
    //购买数量
    private Integer quantity;
    //活动id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long activityId;
    //商品版本号
    private Long version;
    //订单任务id
    private String orderTaskId = "";

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }
}
