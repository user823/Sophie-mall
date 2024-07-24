package com.sophie.sophiemall.seckill.reservation.application.cache;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.cache.service.SeckillCacheService;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;

import java.util.List;

public interface SeckillReservationConfigCacheService extends SeckillCacheService {

    /**
     * 根据商品id和版本号获取商品预约配置信息
     */
    SeckillBusinessCache<SeckillReservationConfig> getSeckillReservationConfig(Long goodsId, Long version);

    /**
     * 更新预约人数
     */
    SeckillBusinessCache<SeckillReservationConfig> updateSeckillReservationConfigCurrentUserCount(Long goodsId, Integer status, Long version);

    /**
     * 更新商品预约配置缓存
     */
    SeckillBusinessCache<SeckillReservationConfig> tryUpdateSeckillReservationConfigCacheByLock(Long goodsId, boolean doubleCheck);

    /**
     * 获取预约配置列表
     */
    SeckillBusinessCache<List<SeckillReservationConfig>> getSeckillReservationConfigList(Long version);

    /**
     * 更新预约配置列表
     */
    SeckillBusinessCache<List<SeckillReservationConfig>> tryUpdateSeckillReservationConfigListCacheByLock(boolean doubleCheck);

}
