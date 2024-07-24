package com.sophie.sophiemall.seckill.goods.application.cache;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.cache.service.SeckillCacheService;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;

public interface SeckillGoodsCacheService extends SeckillCacheService {

    /**
     * 获取商品信息
     */
    SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version);

    /**
     * 更新缓存
     */
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck);

    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
