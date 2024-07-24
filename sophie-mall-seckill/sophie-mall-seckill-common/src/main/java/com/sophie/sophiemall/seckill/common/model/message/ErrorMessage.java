package com.sophie.sophiemall.seckill.common.model.message;

public class ErrorMessage extends TopicMessage {
    //全局事务编号
    private Long txNo;
    //商品id
    private Long goodsId;
    //购买数量
    private Integer quantity;
    //下单的类型
    private String placeOrderType;
    //是否扣减了缓存库存
    private Boolean exception;
    //库存分桶编号
    private Integer bucketSerialNo;
    //用户id
    private Long userId;
    //订单任务id
    private String orderTaskId;

    public ErrorMessage() {
    }

    public ErrorMessage(String destination, Long txNo, Long goodsId, Integer quantity, String placeOrderType,
                        Boolean exception, Integer bucketSerialNo, Long userId, String orderTaskId) {
        super(destination);
        this.txNo = txNo;
        this.goodsId = goodsId;
        this.quantity = quantity;
        this.placeOrderType = placeOrderType;
        this.exception = exception;
        this.bucketSerialNo = bucketSerialNo;
        this.userId = userId;
        this.orderTaskId = orderTaskId;
    }

    public Long getTxNo() {
        return txNo;
    }

    public void setTxNo(Long txNo) {
        this.txNo = txNo;
    }

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

    public String getPlaceOrderType() {
        return placeOrderType;
    }

    public void setPlaceOrderType(String placeOrderType) {
        this.placeOrderType = placeOrderType;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }

    public Integer getBucketSerialNo() {
        return bucketSerialNo;
    }

    public void setBucketSerialNo(Integer bucketSerialNo) {
        this.bucketSerialNo = bucketSerialNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }
}
