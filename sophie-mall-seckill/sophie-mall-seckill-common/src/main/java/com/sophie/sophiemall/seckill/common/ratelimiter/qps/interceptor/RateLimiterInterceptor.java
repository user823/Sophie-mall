package com.sophie.sophiemall.seckill.common.ratelimiter.qps.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.ratelimiter.qps.annotation.SeckillRateLimiter;
import com.sophie.sophiemall.seckill.common.ratelimiter.qps.bean.SophieRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@ConditionalOnProperty(prefix = "rate.limit.local.qps", name = "enabled", havingValue = "true")
public class RateLimiterInterceptor implements EnvironmentAware {
    private final Logger logger = LoggerFactory.getLogger(RateLimiterInterceptor.class);
    private static final Map<String, SophieRateLimiter> BH_RATE_LIMITER_MAP = new ConcurrentHashMap<>();
    private Environment environment;

    @Value("${rate.limit.local.qps.default.permitsPerSecond:1000}")
    private double defaultPermitsPerSecond;

    @Value("${rate.limit.local.qps.default.timeout:1}")
    private long defaultTimeout;

    @Pointcut("@annotation(seckillRateLimiter)")
    public void pointCut(SeckillRateLimiter seckillRateLimiter){

    }

    @Around(value = "pointCut(seckillRateLimiter)")
    public Object around(ProceedingJoinPoint pjp, SeckillRateLimiter seckillRateLimiter) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        String rateLimitName = environment.resolvePlaceholders(seckillRateLimiter.name());
        if (StrUtil.isEmpty(rateLimitName) || rateLimitName.contains("${")) {
            rateLimitName = className + "-" + methodName;
        }
        SophieRateLimiter rateLimiter = this.getRateLimiter(rateLimitName, seckillRateLimiter);
        boolean success = rateLimiter.tryAcquire();
        Object[] args = pjp.getArgs();
        if (success){
            return pjp.proceed(args);
        }
        logger.error("around|访问接口过于频繁|{},{}", className, methodName);
        throw new SeckillException(ErrorCode.RETRY_LATER);
    }

    /**
     * 获取BHRateLimiter对象
     */
    private SophieRateLimiter getRateLimiter(String rateLimitName, SeckillRateLimiter seckillRateLimiter) {
        //先从Map缓存中获取
        SophieRateLimiter bhRateLimiter = BH_RATE_LIMITER_MAP.get(rateLimitName);
        //如果获取的bhRateLimiter为空，则创建bhRateLimiter，注意并发，创建的时候需要加锁
        if (bhRateLimiter == null){
            final String finalRateLimitName = rateLimitName.intern();
            synchronized (finalRateLimitName){
                //double check
                bhRateLimiter = BH_RATE_LIMITER_MAP.get(rateLimitName);
                //获取的bhRateLimiter再次为空
                if (bhRateLimiter == null){
                    double permitsPerSecond = seckillRateLimiter.permitsPerSecond() <= 0 ? defaultPermitsPerSecond : seckillRateLimiter.permitsPerSecond();
                    long timeout = seckillRateLimiter.timeout() <= 0 ? defaultTimeout : seckillRateLimiter.timeout();
                    TimeUnit timeUnit = seckillRateLimiter.timeUnit();
                    bhRateLimiter = new SophieRateLimiter(RateLimiter.create(permitsPerSecond), timeout, timeUnit);
                    BH_RATE_LIMITER_MAP.putIfAbsent(rateLimitName, bhRateLimiter);
                }
            }
        }
        return bhRateLimiter;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}