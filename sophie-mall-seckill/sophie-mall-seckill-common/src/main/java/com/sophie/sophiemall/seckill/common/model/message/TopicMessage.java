package com.sophie.sophiemall.seckill.common.model.message;

public class TopicMessage {
    /**
     * 消息目的地，可以是消息主题
     */
    private String destination;

    public TopicMessage(){
    }

    public TopicMessage(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}