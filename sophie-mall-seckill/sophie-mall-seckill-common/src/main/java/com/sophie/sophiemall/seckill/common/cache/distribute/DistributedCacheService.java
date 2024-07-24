package com.sophie.sophiemall.seckill.common.cache.distribute;

import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;
public interface DistributedCacheService {

    void put(String key, String value);

    void put(String key, Object value);

    void put(String key, Object value, long timeout, TimeUnit unit);

    void put(String key, Object value, long expireTime);

    <T> T getObject(String key, Class<T> targetClass);

    Object getObject(String key);

    String getString(String key);

    <T> List<T> getList(String key, Class<T> targetClass);

    Boolean delete(String key);

    Boolean deleteKeyPrefix(String prefix);

    Boolean hasKey(String key);

    Long addSet(String key, Object... values);

    Long removeSet(String key, Object... values);

    Boolean isMemberSet(String key, Object o);

    /**
     * 执行Lua脚本
     */
    Long execute(RedisScript<Long> script, List<String> keys, Object... args);

    /**
     * 扣减内存中的数据
     */
    default Long decrement(String key, long delta){
        return null;
    }
    /**
     * 增加内存中的数据
     */
    default Long increment(String key, long delta){
        return null;
    }

    /**
     * 使用Lua脚本扣减库存
     */
    default Long decrementByLua(String key, Integer quantity){
        return null;
    }
    /**
     * 使用Lua脚本增加库存
     */
    default Long incrementByLua(String key, Integer quantity){
        return null;
    }

    /**
     * 使用Lua脚本初始化库存
     */
    default Long initByLua(String key, Integer quantity){
        return null;
    }

    /**
     * 检测是否已经恢复缓存的库存数据
     */
    default Long checkExecute(String key, Long seconds){
        return null;
    }

    /**
     * 获取下单许可
     */
    default Long takeOrderToken(String key){
        return null;
    }
    /**
     * 恢复下单许可
     */
    default Long recoverOrderToken(String key){
        return null;
    }

    /**
     * 扣减分桶库存
     */
    default Long decrementBucketStock(List<String> keys, Integer quantity){
        return null;
    }

    /**
     * 恢复分桶库存
     */
    default Long incrementBucketStock(List<String> keys, Integer quantity){
        return null;
    }
}