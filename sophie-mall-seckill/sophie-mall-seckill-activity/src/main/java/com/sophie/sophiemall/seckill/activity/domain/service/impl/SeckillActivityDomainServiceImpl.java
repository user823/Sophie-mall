package com.sophie.sophiemall.seckill.activity.domain.service.impl;


import com.alibaba.fastjson.JSON;
import com.sophie.sophiemall.seckill.activity.domain.event.SeckillActivityEvent;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.activity.domain.repository.SeckillActivityRepository;
import com.sophie.sophiemall.seckill.activity.domain.service.SeckillActivityDomainService;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillActivityStatus;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityDomainServiceImpl.class);
    @Autowired
    private SeckillActivityRepository seckillActivityRepository;
    @Autowired
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        logger.info("activityPublish|发布秒杀活动|{}", JSON.toJSON(seckillActivity));
        if (seckillActivity == null || !seckillActivity.validateParams()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);
        logger.info("activityPublish|秒杀活动已发布|{}", seckillActivity.getId());

        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(seckillActivity.getId(), seckillActivity.getStatus(), getTopicEvent());
        messageSenderService.send(seckillActivityEvent);
        logger.info("activityPublish|秒杀活动事件已发布|{}", JSON.toJSON(seckillActivityEvent));
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        if (id == null){
            throw new SeckillException(ErrorCode.PASSWORD_IS_NULL);
        }
        return seckillActivityRepository.getSeckillActivityById(id);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        logger.info("activityPublish|更新秒杀活动状态|{},{}", status, id);
        if (status == null || id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivityRepository.updateStatus(status, id);
        logger.info("activityPublish|发布秒杀活动状态事件|{},{}", status, id);
        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(id, status, getTopicEvent());
        messageSenderService.send(seckillActivityEvent);
        logger.info("activityPublish|秒杀活动事件已发布|{}", id);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY : SeckillConstants.TOPIC_EVENT_COLA;
    }
}

