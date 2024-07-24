package com.sophie.sophiemall.seckill.stock.application.service;

import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;

public interface SeckillStockBucketArrangementService {

    /**
     * 编码分桶库存
     * @param goodsId 商品id
     * @param stock 库存数量
     * @param bucketsQuantity 分桶数量
     * @param assignmentMode 编排模式
     */
    void arrangeStockBuckets(Long goodsId, Integer stock, Integer bucketsQuantity, Integer assignmentMode);

    /**
     * 通过商品id获取库存分桶信息
     */
    SeckillStockBucketDTO getSeckillStockBucketDTO(Long goodsId, Long version);
}
