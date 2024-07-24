package com.sophie.sophiemall.seckill.activity.domain.service;

import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;

import java.util.Date;
import java.util.List;
public interface SeckillActivityDomainService {

    /**
     * 保存活动信息
     */
    void saveSeckillActivity(SeckillActivity seckillActivity);

    /**
     * 活动列表
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 获取正在进行中的活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status);

    /**
     * 根据id获取活动信息
     */
    SeckillActivity getSeckillActivityById(Long id);

    /**
     * 修改状态
     */
    void updateStatus(Integer status, Long id);
}
