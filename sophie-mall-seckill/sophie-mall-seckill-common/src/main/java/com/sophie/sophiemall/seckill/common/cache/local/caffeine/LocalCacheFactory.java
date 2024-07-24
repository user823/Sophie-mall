package com.sophie.sophiemall.seckill.common.cache.local.caffeine;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class LocalCacheFactory {

    public static <K, V> Cache<K, V> getLocalCache(){
        return Caffeine.newBuilder().initialCapacity(15).expireAfterWrite(10, TimeUnit.SECONDS).build();
    }
}