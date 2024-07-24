package com.sophie.sophiemall.seckill.stock.application.service;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;

public interface SeckillStockBucketService {

    /**
     * 编排库存
     */
    void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand);

    /**
     * 获取库存分桶
     */
    SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version);

    /**
     * 获取商品可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);

    /**
     * 获取商品的库存信息
     */
    SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version);

    /**
     * 扣减商品库存
     */
    boolean decreaseStock(TxMessage txMessage);
}
