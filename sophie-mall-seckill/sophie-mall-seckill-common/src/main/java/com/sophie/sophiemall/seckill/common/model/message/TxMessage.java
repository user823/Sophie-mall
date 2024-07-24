package com.sophie.sophiemall.seckill.common.model.message;

import java.math.BigDecimal;
public class TxMessage extends ErrorMessage {
    //活动id
    private Long activityId;
    //商品版本号
    private Long version;
    //商品名称
    private String goodsName;
    //秒杀活动价格
    private BigDecimal activityPrice;

    public TxMessage() {
    }

    public TxMessage(String destination, Long txNo, Long goodsId, Integer quantity, Long activityId, Long version, Long userId, String goodsName,
                     BigDecimal activityPrice, String placeOrderType, Boolean exception, Integer bucketSerialNo, String orderTaskId) {

        super(destination, txNo, goodsId, quantity, placeOrderType, exception, bucketSerialNo, userId, orderTaskId);
        this.activityId = activityId;
        this.version = version;
        this.goodsName = goodsName;
        this.activityPrice = activityPrice;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(BigDecimal activityPrice) {
        this.activityPrice = activityPrice;
    }
}
