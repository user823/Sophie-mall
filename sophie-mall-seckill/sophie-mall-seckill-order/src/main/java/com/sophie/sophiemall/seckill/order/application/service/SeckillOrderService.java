package com.sophie.sophiemall.seckill.order.application.service;

import com.sophie.sophiemall.seckill.common.model.dto.order.SeckillOrderSubmitDTO;
import com.sophie.sophiemall.seckill.common.model.message.ErrorMessage;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

//    /**
//     * 保存订单
//     */
//    Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId);

    /**
     * 删除订单
     */
    void deleteOrder(ErrorMessage errorMessage);

    /**
     * 根据任务id获取订单号
     */
    SeckillOrderSubmitDTO getSeckillOrderSubmitDTOByTaskId(String taskId, Long userId, Long goodsId);
}
