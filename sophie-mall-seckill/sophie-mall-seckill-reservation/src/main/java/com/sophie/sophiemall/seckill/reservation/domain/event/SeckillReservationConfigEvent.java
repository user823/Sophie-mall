package com.sophie.sophiemall.seckill.reservation.domain.event;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;

public class SeckillReservationConfigEvent extends SeckillBaseEvent {

    public SeckillReservationConfigEvent(Long id, Integer status,  @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
    }
}
