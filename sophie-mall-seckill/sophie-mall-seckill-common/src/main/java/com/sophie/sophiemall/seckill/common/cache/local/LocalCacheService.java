package com.sophie.sophiemall.seckill.common.cache.local;

public interface LocalCacheService<K, V> {

    void put(K key, V value);

    V getIfPresent(K key);

    void delete(K key);
}
