package com.sophie.sophiemall.seckill.common.cache.service;

public interface SeckillCacheService {
    /**
     * 构建缓存的key
     */
    default String buildCacheKey(Object key){
        return key == null ? "" : key.toString();
    }
}