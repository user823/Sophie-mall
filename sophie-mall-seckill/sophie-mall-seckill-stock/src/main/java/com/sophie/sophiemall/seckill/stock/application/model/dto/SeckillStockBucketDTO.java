package com.sophie.sophiemall.seckill.stock.application.model.dto;

import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.util.List;

public class SeckillStockBucketDTO extends SeckillStockDTO {
    private static final long serialVersionUID = 2704697441525819036L;
    //分桶数量
    private Integer bucketsQuantity;
    //库存分桶信息
    private List<SeckillStockBucket> buckets;

    public SeckillStockBucketDTO() {
    }

    public SeckillStockBucketDTO(Integer totalStock, Integer availableStock, List<SeckillStockBucket> buckets) {
        super(totalStock, availableStock);
        this.buckets = buckets;
        this.bucketsQuantity = buckets.size();
    }

    public List<SeckillStockBucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<SeckillStockBucket> buckets) {
        this.buckets = buckets;
    }

    public Integer getBucketsQuantity() {
        return bucketsQuantity;
    }

    public void setBucketsQuantity(Integer bucketsQuantity) {
        this.bucketsQuantity = bucketsQuantity;
    }
}