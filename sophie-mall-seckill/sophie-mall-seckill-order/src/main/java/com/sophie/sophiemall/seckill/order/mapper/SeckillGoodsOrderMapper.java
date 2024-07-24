package com.sophie.sophiemall.seckill.order.mapper;

import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeckillGoodsOrderMapper {

    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 删除订单数据
     */
    void deleteOrder(@Param("goodsId") Long goodsId, @Param("orderId") Long orderId);
}
