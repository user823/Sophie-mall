package com.sophie.sophiemall.seckill.activity.application.dubbo;

import com.sophie.sophiemall.seckill.activity.application.service.SeckillActivityService;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.dubbo.activity.SeckillActivityDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DubboService(version = "1.0.0")
public class SeckillActivityDubboServiceImpl implements SeckillActivityDubboService {
    @Autowired
    private SeckillActivityService seckillActivityService;

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        return seckillActivityService.getSeckillActivity(id, version);
    }
}