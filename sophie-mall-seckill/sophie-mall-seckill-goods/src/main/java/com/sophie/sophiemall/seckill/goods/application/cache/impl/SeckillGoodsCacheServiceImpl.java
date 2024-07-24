package com.sophie.sophiemall.seckill.goods.application.cache.impl;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.local.caffeine.LocalCacheFactory;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.lock.DistributedLock;
import com.sophie.sophiemall.seckill.common.lock.factory.DistributedLockFactory;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.common.utils.string.StringUtil;
import com.sophie.sophiemall.seckill.common.utils.time.SystemClock;
import com.sophie.sophiemall.seckill.dubbo.stock.SeckillStockDubboService;
import com.sophie.sophiemall.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.sophie.sophiemall.seckill.goods.application.cache.SeckillGoodsCacheService;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;
import com.sophie.sophiemall.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillGoodsCacheServiceImpl implements SeckillGoodsCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillGoodsCacheServiceImpl.class);
    private static final Cache<Long, SeckillBusinessCache<SeckillGoods>> localSeckillGoodsCacheService = LocalCacheFactory.getLocalCache();
    private static final Cache<Long, SeckillBusinessCache<Integer>> localAvailableStockCacheService = LocalCacheFactory.getLocalCache();
    //更新活动时获取分布式锁使用
    private static final String SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY_";
    //本地可重入锁
    private final Lock localCacheUpdatelock = new ReentrantLock();
    @DubboReference(version = "1.0.0", check = false)
    private SeckillStockDubboService seckillStockDubboService;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;
    @Value("${place.order.type:lua}")
    private String placeOrderType;

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_GOODS_CACHE_KEY, key);
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version) {
        //从本地缓存中获取数据
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = localSeckillGoodsCacheService.getIfPresent(goodsId);
        if (seckillGoodsCache != null){
            //版本号为空，则直接返回本地缓存中的数据
            if (seckillGoodsCache.getVersion() == null){
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillGoodsCache.getVersion()) <= 0){
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillGoodsCache.getVersion()) > 0){
                return getDistributedCache(goodsId);
            }
        }
        return getDistributedCache(goodsId);
    }

    /**
     * 获取分布式缓存数据
     */
    private SeckillBusinessCache<SeckillGoods> getDistributedCache(Long goodsId) {
        logger.info("SeckillGoodsCache|读取分布式缓存|{}", goodsId);
        //从分布式缓存中获取数据
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = SeckillGoodsBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillGoods.class);
        //分布式缓存中没有数据
        if (seckillGoodsCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillGoodsCache = tryUpdateSeckillGoodsCacheByLock(goodsId, true);
        }
        //获取的数据不为空，并且不需要重试
        if (seckillGoodsCache != null && !seckillGoodsCache.isRetryLater()){
            //获取本地锁，更新本地缓存
            if (localCacheUpdatelock.tryLock()){
                try {
                    localSeckillGoodsCacheService.put(goodsId, seckillGoodsCache);
                    logger.info("SeckillGoodsCache|本地缓存已经更新|{}", goodsId);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillGoodsCache;
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck) {
        logger.info("SeckillGoodsCache|更新分布式缓存|{}", goodsId);
        //获取分布式锁，保证只有一个线程在更新分布式缓存
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            boolean isSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            //未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isSuccess){
                return new SeckillBusinessCache<SeckillGoods>().retryLater();
            }
            SeckillBusinessCache<SeckillGoods> seckillGoodsCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillGoodsCache = SeckillGoodsBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillGoods.class);
                if (seckillGoodsCache != null){
                    return seckillGoodsCache;
                }
            }
            SeckillGoods seckillGoods = seckillGoodsDomainService.getSeckillGoodsId(goodsId);
            if (seckillGoods == null){
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().notExist();
            }else {
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().with(this.getSeckillGoods(seckillGoods)).withVersion(SystemClock.millisClock().now());
            }
            //将数据保存到分布式缓存
            distributedCacheService.put(buildCacheKey(goodsId), JSON.toJSONString(seckillGoodsCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillGoodsCache|分布式缓存已经更新|{}", goodsId);
            return seckillGoodsCache;
        } catch (InterruptedException e) {
            logger.error("SeckillGoodsCache|更新分布式缓存失败|{}", goodsId);
            return new SeckillBusinessCache<SeckillGoods>().retryLater();
        }finally {
            lock.unlock();
        }
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
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        SeckillBusinessCache<Integer> availableStockCache = localAvailableStockCacheService.getIfPresent(goodsId);
        if (availableStockCache != null){
            //版本号为空，则直接返回本地缓存中的数据
            if (availableStockCache.getVersion() == null){
                logger.info("availableStockCache|命中本地缓存|{}", goodsId);
                return availableStockCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(availableStockCache.getVersion()) <= 0){
                logger.info("availableStockCache|命中本地缓存|{}", goodsId);
                return availableStockCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(availableStockCache.getVersion()) > 0){
                return getDistributedAvailableCache(goodsId);
            }
        }
        return getDistributedAvailableCache(goodsId);
    }

    /**
     * 从分布式缓存获取数据
     */
    private SeckillBusinessCache<Integer> getDistributedAvailableCache(Long goodsId) {
        logger.info("SeckillGoodsCache|读取分布式缓存|{}", goodsId);
        //从分布式缓存中获取数据
        String goodsKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(goodsId));
        //获取商品库存
        Integer availableStock = distributedCacheService.getObject(goodsKey, Integer.class);
        if (availableStock == null){
            availableStock = seckillGoodsDomainService.getAvailableStockById(goodsId);
            distributedCacheService.put(goodsKey, availableStock);
        }
        SeckillBusinessCache<Integer> availableStockCache = new SeckillBusinessCache<Integer>().with(availableStock).withVersion(SystemClock.millisClock().now());
        localAvailableStockCacheService.put(goodsId, availableStockCache);
        return availableStockCache;
    }
}
