package com.sophie.sophiemall.seckill.order.application.service;

import com.sophie.sophiemall.seckill.common.model.dto.order.SeckillOrderSubmitDTO;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.application.model.task.SeckillOrderTask;

public interface SeckillSubmitOrderService {

    /**
     * 保存订单
     */
    SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);


    /**
     * 处理订单任务
     */
    default void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask){

    }

    /**
     * 实现基础校验功能
     */
    default void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand){
    }
}
