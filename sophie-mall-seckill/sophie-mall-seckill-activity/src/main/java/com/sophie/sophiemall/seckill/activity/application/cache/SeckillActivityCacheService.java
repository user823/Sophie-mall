package com.sophie.sophiemall.seckill.activity.application.cache;

import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.cache.service.SeckillCacheService;

public interface SeckillActivityCacheService extends SeckillCacheService {

    /**
     * 根据id获取活动信息
     */
    SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId, boolean doubleCheck);
}
