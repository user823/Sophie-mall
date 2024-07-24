package com.sophie.sophiemall.seckill.common.lock;


import java.util.concurrent.TimeUnit;
public interface DistributedLock {

    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    boolean tryLock() throws InterruptedException;

    void lock(long leaseTime, TimeUnit unit);

    void unlock();

    boolean isLocked();

    boolean isHeldByThread(long threadId);

    boolean isHeldByCurrentThread();
}