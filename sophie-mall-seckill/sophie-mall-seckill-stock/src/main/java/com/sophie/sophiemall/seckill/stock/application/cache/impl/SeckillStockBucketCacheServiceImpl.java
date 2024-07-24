package com.sophie.sophiemall.seckill.stock.application.cache.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.local.caffeine.LocalCacheFactory;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.lock.DistributedLock;
import com.sophie.sophiemall.seckill.common.lock.factory.DistributedLockFactory;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.common.utils.string.StringUtil;
import com.sophie.sophiemall.seckill.common.utils.time.SystemClock;
import com.sophie.sophiemall.seckill.stock.application.builder.SeckillStockBucketBuilder;
import com.sophie.sophiemall.seckill.stock.application.cache.SeckillStockBucketCacheService;
import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import com.sophie.sophiemall.seckill.stock.domain.model.entity.SeckillStockBucket;
import com.sophie.sophiemall.seckill.stock.domain.service.SeckillStockBucketDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillStockBucketCacheServiceImpl implements SeckillStockBucketCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillStockBucketCacheServiceImpl.class);
    private static final Cache<Long, SeckillBusinessCache<SeckillStockBucketDTO>> localSeckillStockBucketCacheService = LocalCacheFactory.getLocalCache();
    //更新活动时获取分布式锁使用
    private static final String SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY_";
    //本地可重入锁
    private final Lock localCacheUpdatelock = new ReentrantLock();
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private SeckillStockBucketDomainService seckillStockBucketDomainService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_STOCK_CACHE_KEY, key);
    }

    @Override
    public SeckillBusinessCache<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version) {
        //从本地缓存获取
        SeckillBusinessCache<SeckillStockBucketDTO> seckillStockBucketCache = localSeckillStockBucketCacheService.getIfPresent(goodsId);
        if (seckillStockBucketCache != null){
            //版本号为空，则直接返回本地缓存中的数据
            if (seckillStockBucketCache.getVersion() == null){
                logger.info("seckillStockBucketCache|命中本地缓存|{}", goodsId);
                return seckillStockBucketCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillStockBucketCache.getVersion()) <= 0){
                logger.info("seckillStockBucketCache|命中本地缓存|{}", goodsId);
                return seckillStockBucketCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillStockBucketCache.getVersion()) > 0){
                return getDistributedCache(goodsId);
            }
        }
        return getDistributedCache(goodsId);
    }
    //从分布式缓存中获取数据
    private SeckillBusinessCache<SeckillStockBucketDTO> getDistributedCache(Long goodsId) {
        logger.info("seckillStockBucketCache|读取分布式缓存|{}", goodsId);
        //从分布式缓存中获取数据
        SeckillBusinessCache<SeckillStockBucketDTO> seckillStockBucketCache = SeckillStockBucketBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillStockBucketDTO.class);
        //分布式缓存中没有数据
        if (seckillStockBucketCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillStockBucketCache = tryUpdateSeckillStockBucketCacheByLock(goodsId, true);
        }
        //获取的数据不为空，并且不需要重试
        if (seckillStockBucketCache != null && !seckillStockBucketCache.isRetryLater()){
            //获取本地锁，更新本地缓存
            if (localCacheUpdatelock.tryLock()){
                try {
                    localSeckillStockBucketCacheService.put(goodsId, seckillStockBucketCache);
                    logger.info("SeckillGoodsCache|本地缓存已经更新|{}", goodsId);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillStockBucketCache;

    }

    @Override
    public SeckillBusinessCache<SeckillStockBucketDTO> tryUpdateSeckillStockBucketCacheByLock(Long goodsId, boolean doubleCheck) {
        logger.info("seckillStockBucketCache|更新分布式缓存|{}", goodsId);
        //获取分布式锁，保证只有一个线程在更新分布式缓存
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            boolean isSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            //未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isSuccess){
                return new SeckillBusinessCache<SeckillStockBucketDTO>().retryLater();
            }
            SeckillBusinessCache<SeckillStockBucketDTO> seckillGoodsCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillGoodsCache = SeckillStockBucketBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillStockBucketDTO.class);
                if (seckillGoodsCache != null){
                    return seckillGoodsCache;
                }
            }
            SeckillStockBucketDTO seckillStockBucketDTO = this.getSeckillStockBucketDTO(goodsId);
            if (seckillStockBucketDTO == null){
                seckillGoodsCache = new SeckillBusinessCache<SeckillStockBucketDTO>().notExist();
            }else {
                seckillGoodsCache = new SeckillBusinessCache<SeckillStockBucketDTO>().with(seckillStockBucketDTO).withVersion(SystemClock.millisClock().now());
            }
            //将数据保存到分布式缓存
            distributedCacheService.put(buildCacheKey(goodsId), JSON.toJSONString(seckillGoodsCache), SeckillConstants.FIVE_MINUTES);
            logger.info("seckillStockBucketCache|分布式缓存已经更新|{}", goodsId);
            return seckillGoodsCache;
        } catch (InterruptedException e) {
            logger.error("seckillStockBucketCache|更新分布式缓存失败|{}", goodsId);
            return new SeckillBusinessCache<SeckillStockBucketDTO>().retryLater();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        SeckillBusinessCache<SeckillStockBucketDTO> seckillBusinessCache = this.getTotalStockBuckets(goodsId, version);
        if (seckillBusinessCache == null || !seckillBusinessCache.isExist() || seckillBusinessCache.isRetryLater() || seckillBusinessCache.getData() == null){
            return new SeckillBusinessCache<Integer>().notExist();
        }
        return new SeckillBusinessCache<Integer>().with(seckillBusinessCache.getData().getAvailableStock()).withVersion(SystemClock.millisClock().now());
    }

    @Override
    public SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version) {
        SeckillBusinessCache<SeckillStockBucketDTO> seckillBusinessCache = this.getTotalStockBuckets(goodsId, version);
        if (seckillBusinessCache == null || !seckillBusinessCache.isExist() || seckillBusinessCache.isRetryLater() || seckillBusinessCache.getData() == null){
            return new SeckillBusinessCache<SeckillStockDTO>().notExist();
        }
        //总库存
        Integer totalStock = seckillBusinessCache.getData().getTotalStock();
        //可用库存
        Integer availableStock = seckillBusinessCache.getData().getAvailableStock();
        return new SeckillBusinessCache<SeckillStockDTO>().with(new SeckillStockDTO(totalStock, availableStock)).withVersion(SystemClock.millisClock().now());
    }

    private SeckillStockBucketDTO getSeckillStockBucketDTO(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        List<SeckillStockBucket> buckets = seckillStockBucketDomainService.getBucketsByGoodsId(goodsId);
        if (CollectionUtil.isEmpty(buckets)){
            logger.error("getSeckillStockBucketDTO|暂无可用库存|{}", goodsId);
            throw new SeckillException(ErrorCode.ORDER_TOKENS_NOT_AVAILABLE);
        }
        int availableStock = buckets.stream().mapToInt(SeckillStockBucket::getAvailableStock).sum();
        int totalStock = buckets.stream().mapToInt(SeckillStockBucket::getInitialStock).sum();
        return new SeckillStockBucketDTO(totalStock, availableStock, buckets);
    }
}
