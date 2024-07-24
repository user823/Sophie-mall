package com.sophie.sophiemall.seckill.activity.repository;

import com.sophie.sophiemall.seckill.activity.mapper.SeckillActivityMapper;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.activity.domain.repository.SeckillActivityRepository;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    public int saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillActivityMapper.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityMapper.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityMapper.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityMapper.getSeckillActivityById(id);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillActivityMapper.updateStatus(status, id);
    }
}
