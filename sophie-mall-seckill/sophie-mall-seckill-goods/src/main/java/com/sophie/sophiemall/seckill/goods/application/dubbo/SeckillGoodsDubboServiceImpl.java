package com.sophie.sophiemall.seckill.goods.application.dubbo;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.dubbo.goods.SeckillGoodsDubboService;
import com.sophie.sophiemall.seckill.goods.application.service.SeckillGoodsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {
    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsDubboServiceImpl.class);
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        return seckillGoodsService.getSeckillGoods(id, version);
    }

    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        return seckillGoodsService.updateDbAvailableStock(count, id);
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return seckillGoodsService.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long goodsId) {
        return seckillGoodsService.getAvailableStockById(goodsId);
    }

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        logger.info("调用商品Dubbo服务");
        return seckillGoodsService.getAvailableStock(goodsId, version);
    }
}
