package com.sophie.sophiemall.seckill.reservation.domain.event;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;

public class SeckillReservationUserEvent extends SeckillBaseEvent {
    //商品id
    private Long goodsId;

    public SeckillReservationUserEvent(Long id, Long goodsId, Integer status, @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
        this.goodsId = goodsId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
