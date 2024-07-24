package com.sophie.sophiemall.seckill.stock.application.cache;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.cache.service.SeckillCacheService;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;

public interface SeckillStockBucketCacheService extends SeckillCacheService {

    /**
     * 缓存库存分桶信息
     */
    SeckillBusinessCache<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version);

    /**
     * 更新缓存
     */
    SeckillBusinessCache<SeckillStockBucketDTO> tryUpdateSeckillStockBucketCacheByLock(Long goodsId, boolean doubleCheck);

    /**
     * 获取商品可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);

    /**
     * 获取商品的库存信息
     */
    SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version);
}