package com.sophie.sophiemall.seckill.goods.repository;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;
import com.sophie.sophiemall.seckill.goods.domain.repository.SeckillGoodsRepository;
import com.sophie.sophiemall.seckill.goods.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillGoodsRepositoryImpl implements SeckillGoodsRepository {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public int saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsMapper.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsMapper.getSeckillGoodsId(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsMapper.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillGoodsMapper.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.updateAvailableStock(count, id);
    }

    @Override
    public int incrementAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.incrementAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsMapper.getAvailableStockById(id);
    }
}
