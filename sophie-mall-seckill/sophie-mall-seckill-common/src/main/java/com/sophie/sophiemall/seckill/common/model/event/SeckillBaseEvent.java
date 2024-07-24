package com.sophie.sophiemall.seckill.common.model.event;

import com.sophie.sophiemall.seckill.common.model.message.TopicMessage;
public class SeckillBaseEvent extends TopicMessage {

    private Long id;
    private Integer status;

    public SeckillBaseEvent(Long id, Integer status, String topicEvent) {
        super(topicEvent);
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
