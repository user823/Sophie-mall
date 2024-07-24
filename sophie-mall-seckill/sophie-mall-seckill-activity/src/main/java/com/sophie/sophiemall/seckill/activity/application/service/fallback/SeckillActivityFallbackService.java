package com.sophie.sophiemall.seckill.activity.application.service.fallback;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillActivityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillActivityFallbackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillActivityFallbackService.class);

    public static SeckillActivityDTO getSeckillActivityBlockHandler(Long id, Long version, BlockException e){
        LOGGER.info("getSeckillActivityBlockHandler|处理lockHandler|{},{},{}",id, version, e.getMessage());
        Date date = new Date();
        return new SeckillActivityDTO(-1001L, "BlockHandler活动", date, date, SeckillActivityStatus.OFFLINE.getCode(), "BlockHandler活动", 0L);
    }

    public static SeckillActivityDTO getSeckillActivityFallback(Long id, Long version, Throwable t){
        LOGGER.info("getSeckillActivityBlockHandler|处理Fallback|{},{},{}", id, version, t.getMessage());
        Date date = new Date();
        return new SeckillActivityDTO(-1001L, "Fallback活动", date, date, SeckillActivityStatus.OFFLINE.getCode(), "Fallback活动", 0L);
    }
}
