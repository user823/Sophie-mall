package com.sophie.sophiemall.seckill.user.mapper;

import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;
import org.apache.ibatis.annotations.Param;

public interface SeckillUserMapper {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(@Param("userName") String userName);
}
