package com.sophie.sophiemall.seckill.user.application.service;

import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;

public interface SeckillUserService {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);

    /**
     * 异步方法
     */
    void asyncMethod();
}
