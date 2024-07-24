package com.sophie.sophiemall.seckill.goods.domain.event;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sophie.sophiemall.seckill.common.model.event.SeckillBaseEvent;

public class SeckillGoodsEvent extends SeckillBaseEvent {
    private Long activityId;


    public SeckillGoodsEvent(Long id, Long activityId, Integer status,  @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}