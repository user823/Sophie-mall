package com.sophie.sophiemall.seckill.stock.application.dubbo;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.stock.SeckillStockDTO;
import com.sophie.sophiemall.seckill.dubbo.stock.SeckillStockDubboService;
import com.sophie.sophiemall.seckill.stock.application.service.SeckillStockBucketService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DubboService(version = "1.0.0")
public class SeckillStockDubboServiceImpl implements SeckillStockDubboService {
    private final Logger logger = LoggerFactory.getLogger(SeckillStockDubboServiceImpl.class);
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketService.getAvailableStock(goodsId, version);
    }

    @Override
    public SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketService.getSeckillStock(goodsId, version);
    }
}
