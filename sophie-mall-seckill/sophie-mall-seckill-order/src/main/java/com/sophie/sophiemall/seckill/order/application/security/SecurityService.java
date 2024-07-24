package com.sophie.sophiemall.seckill.order.application.security;

public interface SecurityService {

    /**
     * 对用户进行风控处理
     */
    boolean securityPolicy(Long userId);
}
