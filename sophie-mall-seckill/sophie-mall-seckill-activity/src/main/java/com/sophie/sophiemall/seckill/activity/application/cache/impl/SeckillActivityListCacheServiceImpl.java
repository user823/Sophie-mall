package com.sophie.sophiemall.seckill.activity.application.cache.impl;

import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.activity.application.builder.SeckillActivityBuilder;
import com.sophie.sophiemall.seckill.activity.application.cache.SeckillActivityListCacheService;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.activity.domain.service.SeckillActivityDomainService;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.cache.local.LocalCacheService;
import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.lock.DistributedLock;
import com.sophie.sophiemall.seckill.common.lock.factory.DistributedLockFactory;
import com.sophie.sophiemall.seckill.common.utils.string.StringUtil;
import com.sophie.sophiemall.seckill.common.utils.time.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillActivityListCacheServiceImpl implements SeckillActivityListCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillActivityListCacheServiceImpl.class);
    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillActivity>>> localCacheService;
    //分布式锁的key
    private static final String SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_";
    private static final String SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY_VERSION_2 = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_VERSION_2_";
    //本地锁
    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_ACTIVITIES_CACHE_KEY, key);
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version) {
        //获取本地缓存
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = localCacheService.getIfPresent(status.longValue());
        if (seckillActivitiyListCache != null){
            if (version == null){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            //传递过来的版本小于或等于缓存中的版本号
            if (version.compareTo(seckillActivitiyListCache.getVersion()) <= 0){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            if (version.compareTo(seckillActivitiyListCache.getVersion()) > 0){
                return getDistributedCache(status);
            }
        }
        return getDistributedCache(status);
    }

    /**
     * 获取分布式缓存中的数据
     */
    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status) {
        logger.info("SeckillActivitesCache|读取分布式缓存|{}", status);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(status)), SeckillActivity.class);
        if (seckillActivitiyListCache == null){
            seckillActivitiyListCache = tryUpdateSeckillActivityCacheByLock(status, true);
        }
        if (seckillActivitiyListCache != null && !seckillActivitiyListCache.isRetryLater()){
            if (localCacheUpdatelock.tryLock()){
                try {
                    localCacheService.put(status.longValue(), seckillActivitiyListCache);
                    logger.info("SeckillActivitesCache|本地缓存已经更新|{}", status);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivitiyListCache;
    }

    /**
     * 根据状态更新分布式缓存数据
     */
    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status, boolean doubleCheck) {
        logger.info("SeckillActivitesCache|更新分布式缓存|{}", status);
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLockSuccess){
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillActivitiyListCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(status)), SeckillActivity.class);
                if (seckillActivitiyListCache != null){
                    return seckillActivitiyListCache;
                }
            }
            List<SeckillActivity> seckillActivityList = seckillActivityDomainService.getSeckillActivityList(status);
            if (seckillActivityList == null){
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            }else {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().with(seckillActivityList).withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivitiyListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitesCache|分布式缓存已经更新|{}", status);
            return seckillActivitiyListCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitesCache|更新分布式缓存失败|{}", status);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Date currentTime, Integer status, Long version) {
        //获取本地缓存
        long key = currentTime.getTime() + status.longValue();
        //获取本地缓存中的数据
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = localCacheService.getIfPresent(key);
        if (seckillActivitiyListCache != null){
            if (version == null){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", key);
                return seckillActivitiyListCache;
            }
            //传递过来的版本小于或等于缓存中的版本号
            if (version.compareTo(seckillActivitiyListCache.getVersion()) <= 0){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", key);
                return seckillActivitiyListCache;
            }
            if (version.compareTo(seckillActivitiyListCache.getVersion()) > 0){
                return getDistributedCacheByKey(currentTime, status);
            }
        }
        return getDistributedCacheByKey(currentTime, status);
    }

    /**
     * 获取分布式缓存中的数据
     */
    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCacheByKey(Date currentTime, Integer status) {
        //获取本地缓存
        long key = currentTime.getTime() + status.longValue();
        logger.info("SeckillActivitesCache|读取分布式缓存|{}", key);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(key)), SeckillActivity.class);
        if (seckillActivitiyListCache == null){
            seckillActivitiyListCache = tryUpdateSeckillActivityCacheByLock(currentTime, status, true);
        }
        if (seckillActivitiyListCache != null && !seckillActivitiyListCache.isRetryLater()){
            if (localCacheUpdatelock.tryLock()){
                try {
                    localCacheService.put(key, seckillActivitiyListCache);
                    logger.info("SeckillActivitesCache|本地缓存已经更新|{}", key);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivitiyListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Date currentTime, Integer status, boolean doubleCheck) {
        //获取本地缓存
        long key = currentTime.getTime() + status.longValue();
        logger.info("SeckillActivitesCache|更新分布式缓存|{}", key);
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY_VERSION_2.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLockSuccess){
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache;
            //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
            if (doubleCheck){
                seckillActivitiyListCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(key)), SeckillActivity.class);
                if (seckillActivitiyListCache != null){
                    return seckillActivitiyListCache;
                }
            }
            List<SeckillActivity> seckillActivityList = seckillActivityDomainService.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
            if (seckillActivityList == null){
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            }else {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().with(seckillActivityList).withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(key), JSON.toJSONString(seckillActivitiyListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitesCache|分布式缓存已经更新|{}", key);
            return seckillActivitiyListCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitesCache|更新分布式缓存失败|{}", key);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            lock.unlock();
        }
    }
}
