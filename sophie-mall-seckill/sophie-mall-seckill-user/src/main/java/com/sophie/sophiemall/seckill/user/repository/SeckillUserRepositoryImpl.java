package com.sophie.sophiemall.seckill.user.repository;

import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;
import com.sophie.sophiemall.seckill.user.domain.repository.SeckillUserRepository;
import com.sophie.sophiemall.seckill.user.mapper.SeckillUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeckillUserRepositoryImpl implements SeckillUserRepository {

    @Autowired
    private SeckillUserMapper seckillUserMapper;

    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserMapper.getSeckillUserByUserName(userName);
    }
}