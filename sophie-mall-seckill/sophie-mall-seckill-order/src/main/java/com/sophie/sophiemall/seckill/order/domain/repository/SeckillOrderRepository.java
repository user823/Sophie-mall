package com.sophie.sophiemall.seckill.order.domain.repository;

import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

public interface SeckillOrderRepository {

    /**
     * 保存订单
     */
    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据商品id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId);

    /**
     * 以userId作为分片键删除订单
     */
    void deleteOrderShardingUserId(Long orderId, Long userId);

    /**
     * 以goodsId作为分片键删除订单
     */
    void deleteOrderShardingGoodsId(Long orderId, Long goodsId);
}
