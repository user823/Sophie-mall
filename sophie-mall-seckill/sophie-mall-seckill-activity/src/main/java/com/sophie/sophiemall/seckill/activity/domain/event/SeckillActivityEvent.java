package com.sophie.sophiemall.seckill.activity.domain.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;
public class SeckillActivityEvent extends SeckillBaseEvent {

    public SeckillActivityEvent(Long id, Integer status,  @JSONField(name = "destination") String destination) {
        super(id, status, destination);
    }
}
