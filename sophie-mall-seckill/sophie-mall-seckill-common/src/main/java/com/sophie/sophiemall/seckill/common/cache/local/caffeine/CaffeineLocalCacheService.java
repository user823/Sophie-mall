package com.sophie.sophiemall.seckill.common.cache.local.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.sophie.sophiemall.seckill.common.cache.local.LocalCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "local.cache.type", havingValue = "caffeine")
public class CaffeineLocalCacheService<K,V> implements LocalCacheService<K, V> {
    //本地缓存，基于Caffeine实现
    private final Cache<K, V> cache = LocalCacheFactory.getLocalCache();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void delete(K key) {
        cache.invalidate(key);
    }
}
