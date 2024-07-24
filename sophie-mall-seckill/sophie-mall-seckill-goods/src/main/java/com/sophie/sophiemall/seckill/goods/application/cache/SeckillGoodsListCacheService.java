package com.sophie.sophiemall.seckill.goods.application.cache;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.cache.service.SeckillCacheService;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

public interface SeckillGoodsListCacheService extends SeckillCacheService {

    /**
     * 获取缓存中的商品列表
     */
    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);


    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsCacheByLock(Long activityId, boolean doubleCheck);
}
