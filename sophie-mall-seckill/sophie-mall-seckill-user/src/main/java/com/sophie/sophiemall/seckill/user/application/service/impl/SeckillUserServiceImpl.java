package com.sophie.sophiemall.seckill.user.application.service.impl;

import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.user.application.service.SeckillUserService;
import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;
import com.sophie.sophiemall.seckill.user.domain.repository.SeckillUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SeckillUserServiceImpl implements SeckillUserService {
    private final Logger logger = LoggerFactory.getLogger(SeckillUserServiceImpl.class);
    @Autowired
    private SeckillUserRepository seckillUserRepository;
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        logger.info("seckill-user|获取用户信息|{}", userName);
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser != null){
            seckillUser.setPassword("");
        }
        return seckillUser;
    }

    @Async
    @Override
    public void asyncMethod() {
        logger.info("执行了异步任务...");
    }

}
