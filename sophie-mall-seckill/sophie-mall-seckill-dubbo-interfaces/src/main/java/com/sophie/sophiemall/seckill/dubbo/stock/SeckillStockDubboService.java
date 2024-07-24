package com.sophie.sophiemall.seckill.dubbo.stock;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.model.dto.stock.*;

public interface SeckillStockDubboService {
    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);

    /**
     * 获取商品的库存信息
     */
    SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version);
}