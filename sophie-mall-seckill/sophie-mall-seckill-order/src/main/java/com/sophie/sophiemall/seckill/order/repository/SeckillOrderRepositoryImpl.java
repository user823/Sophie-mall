package com.sophie.sophiemall.seckill.order.repository;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;
import com.sophie.sophiemall.seckill.order.domain.repository.SeckillOrderRepository;
import com.sophie.sophiemall.seckill.order.mapper.SeckillGoodsOrderMapper;
import com.sophie.sophiemall.seckill.order.mapper.SeckillUserOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {
    @Autowired
    private SeckillUserOrderMapper seckillUserOrderMapper;
    @Autowired
    private SeckillGoodsOrderMapper seckillGoodsOrderMapper;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        int userResult = seckillUserOrderMapper.saveSeckillOrder(seckillOrder);
        int goodsResult = seckillGoodsOrderMapper.saveSeckillOrder(seckillOrder);
        return userResult == 1 && goodsResult == 1;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillUserOrderMapper.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsOrderMapper.getSeckillOrderByGoodsId(goodsId);
    }

    @Override
    public void deleteOrderShardingUserId(Long orderId, Long userId) {
        if (orderId == null || userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillUserOrderMapper.deleteOrder(userId, orderId);
    }

    @Override
    public void deleteOrderShardingGoodsId(Long orderId, Long goodsId) {
        if (goodsId == null || orderId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoodsOrderMapper.deleteOrder(goodsId, orderId);
    }


}
