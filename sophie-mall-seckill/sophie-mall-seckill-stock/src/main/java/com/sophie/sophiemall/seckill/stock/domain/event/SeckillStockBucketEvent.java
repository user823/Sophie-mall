package com.sophie.sophiemall.seckill.stock.domain.event;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;

public class SeckillStockBucketEvent extends SeckillBaseEvent {

    public SeckillStockBucketEvent(Long id, Integer status,  @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
    }
}
