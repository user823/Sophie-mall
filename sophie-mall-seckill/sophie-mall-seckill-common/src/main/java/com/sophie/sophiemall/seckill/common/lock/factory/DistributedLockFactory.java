package com.sophie.sophiemall.seckill.common.lock.factory;

import com.sophie.sophiemall.seckill.common.lock.DistributedLock;

public interface DistributedLockFactory {

    /**
     * 根据key获取分布式锁
     */
    DistributedLock getDistributedLock(String key);

}