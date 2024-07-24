package com.sophie.sophiemall.seckill.goods.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.local.LocalCacheService;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.lock.DistributedLock;
import com.sophie.sophiemall.seckill.common.lock.factory.DistributedLockFactory;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillGoodsStatus;
import com.sophie.sophiemall.seckill.common.model.message.ErrorMessage;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.common.utils.id.SnowFlakeFactory;
import com.sophie.sophiemall.seckill.dubbo.activity.SeckillActivityDubboService;
import com.sophie.sophiemall.seckill.dubbo.stock.SeckillStockDubboService;
import com.sophie.sophiemall.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.sophie.sophiemall.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.sophie.sophiemall.seckill.goods.application.cache.SeckillGoodsListCacheService;
import com.sophie.sophiemall.seckill.goods.application.command.SeckillGoodsCommand;
import com.sophie.sophiemall.seckill.goods.application.service.SeckillGoodsService;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;
import com.sophie.sophiemall.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsServiceImpl.class);
    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;
    @DubboReference(version = "1.0.0", check = false)
    private SeckillActivityDubboService seckillActivityDubboService;
    @DubboReference(version = "1.0.0", check = false)
    private SeckillStockDubboService seckillStockDubboService;
    @Value("${place.order.type:lua}")
    private String placeOrderType;
    @Autowired
    private LocalCacheService<String, SeckillGoods> localCacheService;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;

    private String lockKeySuffix = "_lock";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommond) {
        if (seckillGoodsCommond == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivityDTO seckillActivity = seckillActivityDubboService.getSeckillActivity(seckillGoodsCommond.getActivityId(), seckillGoodsCommond.getVersion());
        if (seckillActivity == null || seckillActivity.isEmpty()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        if (seckillActivity.isFallback()){
            throw new SeckillException(ErrorCode.SENTINEL_EXCEPTION);
        }
        SeckillGoods seckillGoods = SeckillGoodsBuilder.toSeckillGoods(seckillGoodsCommond);
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsCommond.getInitialStock());
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        boolean success = seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
        if (success){
            //将商品的库存同步到Redis
            distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillGoods.getId())), seckillGoods.getAvailableStock());
            //商品限购同步到Redis
            distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillGoods.getId())), seckillGoods.getLimitNum());
        }
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        //实现多级缓存，先从本地缓存获取
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_KEY_PREFIX, String.valueOf(id));
        //从本地缓存获取数据
        SeckillGoods seckillGoods =  localCacheService.getIfPresent(key);
        //如果本地缓存为空，从Redis获取数据
        if (seckillGoods == null){
            //从Redis获取数据
            seckillGoods = distributedCacheService.getObject(key, SeckillGoods.class);
            //从Redis获取的数据为空
            if (seckillGoods == null){
                //数据库中获取数据
                seckillGoods = seckillGoodsDomainService.getSeckillGoodsId(id);
                if (seckillGoods != null){
                    //处理分桶库存
                    seckillGoods = this.getSeckillGoods(seckillGoods);
                    //将数据缓存到Redis
                    distributedCacheService.put(key, seckillGoods, 10, TimeUnit.MINUTES);
                }
            }
            if (seckillGoods != null){
                localCacheService.put(key, seckillGoods);
            }
        }
        return seckillGoods;
    }

    //兼容分桶库存
    private SeckillGoods getSeckillGoods(SeckillGoods seckillGoods){
        //不是分桶库存模式
        if (!SeckillConstants.PLACE_ORDER_TYPE_BUCKET.equals(placeOrderType)){
            return seckillGoods;
        }
        //是分桶库存模式，获取分桶库存
        SeckillBusinessCache<SeckillStockDTO> seckillStockCache = seckillStockDubboService.getSeckillStock(seckillGoods.getId(), 1L);
        if (seckillStockCache == null || !seckillStockCache.isExist() || seckillStockCache.isRetryLater() || seckillStockCache.getData() == null){
            return seckillGoods;
        }
        seckillGoods.setInitialStock(seckillStockCache.getData().getTotalStock());
        seckillGoods.setAvailableStock(seckillStockCache.getData().getAvailableStock());
        return seckillGoods;
    }

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        if (id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getSeckillGoods(id, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillGoodsCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        //缓存中不存在商品数据
        if (!seckillGoodsCache.isExist()){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(seckillGoodsCache.getVersion());
        return seckillGoodsDTO;
    }


    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        List<SeckillGoods> seckillGoodsList = seckillGoodsDomainService.getSeckillGoodsByActivityId(activityId);
        if (CollectionUtil.isEmpty(seckillGoodsList)){
            return seckillGoodsList;
        }
        return seckillGoodsList.stream().map(this::getSeckillGoods).collect(Collectors.toList());
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        if (activityId == null){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillGoodsListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        if (!seckillGoodsListCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        List<SeckillGoodsDTO> seckillActivityDTOList = seckillGoodsListCache.getData().stream().map((seckillGoods) -> {
            SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
            BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
            seckillGoodsDTO.setVersion(seckillGoodsListCache.getVersion());
            return seckillGoodsDTO;
        }).collect(Collectors.toList());
        return seckillActivityDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer status, Long id) {
        if (status == SeckillGoodsStatus.OFFLINE.getCode()){
            //清空缓存
            this.clearCache(String.valueOf(id));
        }
        seckillGoodsDomainService.updateStatus(status, id);
    }

    /**
     * 清空缓存的商品数据
     */
    private void clearCache(String id){
        //清除缓存中的商品库存
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, id));
        //清除本地缓存中的商品
        localCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_KEY_PREFIX, id));
        //清除Redis缓存中的商品
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_KEY_PREFIX, id));
        //清除商品的限购信息
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, id));
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateAvailableStock(count, id);
    }
    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateDbAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsDomainService.getAvailableStockById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvailableStock(TxMessage txMessage) {
        boolean isUpdate = false;
        String redisKey = SeckillConstants.getKey(SeckillConstants.GOODS_TX_KEY, String.valueOf(txMessage.getTxNo()));
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
            isUpdate = seckillGoodsDomainService.updateAvailableStock(txMessage.getQuantity(), txMessage.getGoodsId());
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
                seckillGoodsDomainService.incrementAvailableStock(txMessage.getQuantity(), txMessage.getGoodsId());
                //清除缓存标识
                distributedCacheService.delete(redisKey);
            }
            //重置isUpdate的值
            isUpdate = false;
            logger.error("updateAvailableStock|抛出异常|{},{}",txMessage.getTxNo(), e.getMessage());
            //发送失败消息给订单微服务
            messageSenderService.send(getErrorMessage(txMessage));
        }finally {
            distributedLock.unlock();
        }
        return isUpdate;
    }

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        return seckillGoodsCacheService.getAvailableStock(goodsId, version);
    }
    /**
     * 发送给订单微服务的错误消息
     */
    private ErrorMessage getErrorMessage(TxMessage txMessage){
        return new ErrorMessage(SeckillConstants.TOPIC_ERROR_MSG, txMessage.getTxNo(), txMessage.getGoodsId(), txMessage.getQuantity(), txMessage.getPlaceOrderType(), txMessage.getException(), txMessage.getBucketSerialNo(), txMessage.getUserId(), txMessage.getOrderTaskId());
    }

}
