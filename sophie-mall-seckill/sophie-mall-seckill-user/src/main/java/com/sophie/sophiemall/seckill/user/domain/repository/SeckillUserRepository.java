package com.sophie.sophiemall.seckill.user.domain.repository;

import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;

public interface SeckillUserRepository {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);
}
