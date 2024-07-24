package com.sophie.sophiemall.seckill.stock.application.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.lock.DistributedLock;
import com.sophie.sophiemall.seckill.common.lock.factory.DistributedLockFactory;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.common.model.message.ErrorMessage;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.stock.application.cache.SeckillStockBucketCacheService;
import com.sophie.sophiemall.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import com.sophie.sophiemall.seckill.stock.application.service.SeckillStockBucketArrangementService;
import com.sophie.sophiemall.seckill.stock.application.service.SeckillStockBucketService;
import com.sophie.sophiemall.seckill.stock.domain.model.dto.SeckillStockBucketDeduction;
import com.sophie.sophiemall.seckill.stock.domain.service.SeckillStockBucketDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class SeckillStockBucketServiceImpl implements SeckillStockBucketService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillStockBucketServiceImpl.class);

    @Autowired
    private DistributedLockFactory distributedLockFactory;
    @Autowired
    private SeckillStockBucketArrangementService seckillStockBucketArrangementService;
    @Autowired
    private SeckillStockBucketCacheService seckillStockBucketCacheService;
    @Autowired
    private SeckillStockBucketDomainService seckillStockBucketDomainService;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private MessageSenderService messageSenderService;
    private String lockKeySuffix = "_lock";
    @Override
    public void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand) {
        if (userId == null || stockBucketWrapperCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        stockBucketWrapperCommand.setUserId(userId);
        if (stockBucketWrapperCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("arrangeBuckets|编排库存分桶|{}", JSON.toJSON(stockBucketWrapperCommand));
        String lockKey = SeckillConstants.getKey(SeckillConstants.getKey(SeckillConstants.GOODS_BUCKET_ARRANGEMENT_KEY, String.valueOf(stockBucketWrapperCommand.getUserId())), String.valueOf(stockBucketWrapperCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        try{
            boolean isLock = lock.tryLock();
            if (!isLock){
                throw new SeckillException(ErrorCode.FREQUENTLY_ERROR);
            }
            //获取到锁，编排库存
            seckillStockBucketArrangementService.arrangeStockBuckets(stockBucketWrapperCommand.getGoodsId(),
                    stockBucketWrapperCommand.getStockBucketCommand().getTotalStock(),
                    stockBucketWrapperCommand.getStockBucketCommand().getBucketsQuantity(),
                    stockBucketWrapperCommand.getStockBucketCommand().getArrangementMode());
            logger.info("arrangeStockBuckets|库存编排完成|{}", stockBucketWrapperCommand.getGoodsId());
        }catch (SeckillException e){
            logger.error("arrangeStockBuckets|库存编排失败|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw e;
        }catch (Exception e){
            logger.error("arrangeStockBuckets|库存编排错误|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw new SeckillException(ErrorCode.BUCKET_CREATE_FAILED);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("stockBucketsSummary|获取库存分桶数据|{}", goodsId);
        return seckillStockBucketArrangementService.getSeckillStockBucketDTO(goodsId, version);
    }

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketCacheService.getAvailableStock(goodsId, version);
    }

    @Override
    public SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketCacheService.getSeckillStock(goodsId, version);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseStock(TxMessage txMessage) {
        boolean isUpdate = false;
        String redisKey = SeckillConstants.getKey(SeckillConstants.STOCK_TX_KEY, String.valueOf(txMessage.getTxNo()));
        String lockKey = redisKey.concat(lockKeySuffix);
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try{
            //未获取到锁，不能更新库存，直接返回false
            if (!distributedLock.tryLock()){
                return isUpdate;
            }
            Boolean decrementStock = distributedCacheService.hasKey(redisKey);
            if (BooleanUtil.isTrue(decrementStock)){
                logger.info("updateAvailableStock|秒杀商品微服务已经扣减过库存|{}", txMessage.getTxNo());
                return true;
            }
            isUpdate = seckillStockBucketDomainService.decreaseStock(new SeckillStockBucketDeduction(txMessage.getGoodsId(), txMessage.getQuantity(), txMessage.getUserId(), txMessage.getBucketSerialNo()));
            //成功扣减库存成功
            if (isUpdate){
                distributedCacheService.put(redisKey, txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
            }else{
                //发送失败消息给订单微服务
                messageSenderService.send(getErrorMessage(txMessage));
            }
        }catch (Exception e){
            //已经扣减了商品库存
            if (isUpdate){
                //回滚数据库库存
                seckillStockBucketDomainService.increaseStock(new SeckillStockBucketDeduction(txMessage.getGoodsId(), txMessage.getQuantity(), txMessage.getUserId(), txMessage.getBucketSerialNo()));
                //清除缓存标识
                distributedCacheService.delete(redisKey);
            }
            //重置标识
            isUpdate = false;
            logger.error("decreaseStock|抛出异常|{},{}",txMessage.getTxNo(), e.getMessage());
            //发送失败消息给订单微服务
            messageSenderService.send(getErrorMessage(txMessage));
        }finally {
            distributedLock.unlock();
        }
        return isUpdate;
    }

    /**
     * 发送给订单微服务的错误消息
     */
    private ErrorMessage getErrorMessage(TxMessage txMessage){
        return new ErrorMessage(SeckillConstants.TOPIC_ERROR_MSG, txMessage.getTxNo(), txMessage.getGoodsId(), txMessage.getQuantity(), txMessage.getPlaceOrderType(), txMessage.getException(), txMessage.getBucketSerialNo(), txMessage.getUserId(), txMessage.getOrderTaskId());
    }
}
