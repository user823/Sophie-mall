package com.sophie.sophiemall.seckill.stock.domain.repository;

import com.sophie.sophiemall.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.util.List;

public interface SeckillStockBucketRepository {

    /**
     * 批量提交商品库存信息
     */
    boolean submitBuckets(Long goodsId, List<SeckillStockBucket> buckets);

    /**
     * 增加库存
     */
    boolean increaseStock(Integer quantity, Integer serialNo, Long goodsId);

    /**
     * 扣减库存
     */
    boolean decreaseStock(Integer quantity, Integer serialNo, Long goodsId);

    /**
     * 根据商品id获取库存分桶列表
     */
    List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId);

    /**
     * 暂停库存
     */
    boolean suspendBuckets(Long goodsId);

    /**
     * 恢复库存
     */
    boolean resumeBuckets(Long goodsId);
}
