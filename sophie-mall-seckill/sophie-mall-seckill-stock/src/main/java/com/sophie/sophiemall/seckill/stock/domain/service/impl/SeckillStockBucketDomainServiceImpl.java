package com.sophie.sophiemall.seckill.stock.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.stock.domain.event.SeckillStockBucketEvent;
import com.sophie.sophiemall.seckill.stock.domain.model.dto.SeckillStockBucketDeduction;
import com.sophie.sophiemall.seckill.stock.domain.model.entity.SeckillStockBucket;
import com.sophie.sophiemall.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
import com.sophie.sophiemall.seckill.stock.domain.repository.SeckillStockBucketRepository;
import com.sophie.sophiemall.seckill.stock.domain.service.SeckillStockBucketDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillStockBucketDomainServiceImpl implements SeckillStockBucketDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillStockBucketDomainServiceImpl.class);
    @Autowired
    private SeckillStockBucketRepository seckillStockBucketRepository;
    @Autowired
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public boolean suspendBuckets(Long goodsId) {
        logger.info("suspendBuckets|禁用库存分桶|{}", goodsId);
        if (goodsId == null || goodsId <= 0){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean success = seckillStockBucketRepository.suspendBuckets(goodsId);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.DISABLED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("suspendBuckets|库存分桶已禁用|{}", goodsId);
        return true;
    }

    @Override
    public boolean resumeBuckets(Long goodsId) {
        logger.info("resumeBuckets|启用库存分桶|{}", goodsId);
        if (goodsId == null || goodsId <= 0){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean success = seckillStockBucketRepository.resumeBuckets(goodsId);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.ENABLED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("resumeBuckets|库存分桶已启用|{}", goodsId);
        return true;
    }

    @Override
    public List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId) {
        if (goodsId == null || goodsId <= 0){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketRepository.getBucketsByGoodsId(goodsId);
    }

    @Override
    public boolean arrangeBuckets(Long goodsId, List<SeckillStockBucket> buckets) {
        logger.info("arrangeBuckets|编排库存分桶|{},{}", goodsId, JSON.toJSONString(buckets));
        if (goodsId == null || goodsId <= 0 || CollectionUtil.isEmpty(buckets)){
            logger.info("arrangeBuckets|库存分桶参数错误|{}", goodsId);
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        //校验数据
        this.checkBuckets(goodsId, buckets);
        //存储分桶数据
        boolean success = seckillStockBucketRepository.submitBuckets(goodsId, buckets);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.ARRANGED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("arrangeBuckets|编排库存分桶已完成|{}", goodsId);
        return true;
    }

    private void checkBuckets(Long goodsId, List<SeckillStockBucket> buckets){
        //校验数据
        buckets.forEach((bucket) -> {
            if (!goodsId.equals(bucket.getGoodsId())){
                throw new SeckillException(ErrorCode.BUCKET_GOODSID_ERROR);
            }
            if (bucket.getInitialStock() == null || bucket.getInitialStock() < 0){
                throw new SeckillException(ErrorCode.BUCKET_INIT_STOCK_ERROR);
            }
            if (bucket.getAvailableStock() == null || bucket.getInitialStock() < 0){
                throw new SeckillException(ErrorCode.BUCKET_AVAILABLE_STOCK_ERROR);
            }
            if (bucket.getInitialStock() < bucket.getAvailableStock()){
                throw new SeckillException(ErrorCode.BUCKET_STOCK_ERROR);
            }
        });
    }

    @Override
    public boolean decreaseStock(SeckillStockBucketDeduction stockDeduction) {
        logger.info("decreaseItemStock|扣减库存|{}", JSON.toJSONString(stockDeduction));
        if (stockDeduction == null || stockDeduction.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean success = seckillStockBucketRepository.decreaseStock(stockDeduction.getQuantity(), stockDeduction.getSerialNo(), stockDeduction.getGoodsId());
        if (success){
            SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(stockDeduction.getGoodsId(), SeckillStockBucketEventType.ENABLED.getCode(), getTopicEvent());
            messageSenderService.send(seckillStockBucketEvent);
        }
        return success;
    }

    @Override
    public boolean increaseStock(SeckillStockBucketDeduction stockDeduction) {
        logger.info("increaseItemStock|恢复库存|{}", JSON.toJSONString(stockDeduction));
        if (stockDeduction == null || stockDeduction.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        boolean success = seckillStockBucketRepository.increaseStock(stockDeduction.getQuantity(), stockDeduction.getSerialNo(), stockDeduction.getGoodsId());
        if (success){
            SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(stockDeduction.getGoodsId(), SeckillStockBucketEventType.ENABLED.getCode(), getTopicEvent());
            messageSenderService.send(seckillStockBucketEvent);
        }
        return success;
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_STOCK : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
