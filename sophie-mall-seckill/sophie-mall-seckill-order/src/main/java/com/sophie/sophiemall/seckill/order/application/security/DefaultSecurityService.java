package com.sophie.sophiemall.seckill.order.application.security;

import org.springframework.stereotype.Service;

@Service
public class DefaultSecurityService implements SecurityService{
    @Override
    public boolean securityPolicy(Long userId) {
        return true;
    }
}