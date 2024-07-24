package com.sophie.sophiemall.seckill.dubbo.activity;

import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;

public interface SeckillActivityDubboService {

    /**
     * 获取活动信息
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);
}
