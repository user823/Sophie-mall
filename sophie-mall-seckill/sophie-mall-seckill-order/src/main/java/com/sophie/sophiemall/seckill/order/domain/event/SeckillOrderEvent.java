package com.sophie.sophiemall.seckill.order.domain.event;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;

public class SeckillOrderEvent extends SeckillBaseEvent {

    public SeckillOrderEvent(Long id, Integer status,  @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
    }
}
