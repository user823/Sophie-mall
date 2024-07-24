package com.sophie.sophiemall.seckill.stock.domain.service;

import com.sophie.sophiemall.seckill.stock.domain.model.dto.SeckillStockBucketDeduction;
import com.sophie.sophiemall.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.util.List;

public interface SeckillStockBucketDomainService {

    /**
     * 暂停库存
     */
    boolean suspendBuckets(Long goodsId);

    /**
     * 恢复库存
     */
    boolean resumeBuckets(Long goodsId);

    /**
     * 根据商品id获取库存分桶列表
     */
    List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId);

    /**
     * 编排库存分桶
     */
    boolean arrangeBuckets(Long goodsId, List<SeckillStockBucket> buckets);

    /**
     * 库存扣减
     */
    boolean decreaseStock(SeckillStockBucketDeduction stockDeduction);

    /**
     * 库存恢复
     */
    boolean increaseStock(SeckillStockBucketDeduction stockDeduction);
}
