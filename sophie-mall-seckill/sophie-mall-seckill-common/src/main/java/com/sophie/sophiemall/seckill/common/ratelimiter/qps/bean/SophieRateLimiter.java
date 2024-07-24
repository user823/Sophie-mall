package com.sophie.sophiemall.seckill.common.ratelimiter.qps.bean;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;
public class SophieRateLimiter {
    //Google RateLimiter限流器
    private RateLimiter rateLimiter;
    //获取令牌超时时间
    private long timeout;
    //获取令牌超时时间单位
    private TimeUnit timeUnit;

    public SophieRateLimiter() {
    }

    public SophieRateLimiter(RateLimiter rateLimiter, long timeout, TimeUnit timeUnit) {
        this.rateLimiter = rateLimiter;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean tryAcquire(){
        return rateLimiter.tryAcquire();
    }

    public boolean tryAcquireWithTimeout(){
        return rateLimiter.tryAcquire(timeout, timeUnit);
    }

    public boolean tryAcquire(int permits){
        return rateLimiter.tryAcquire(permits);
    }

    public boolean tryAcquireWithTimeout(int permits){
        return rateLimiter.tryAcquire(permits, timeout, timeUnit);
    }
}
