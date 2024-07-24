package com.sophie.sophiemall.seckill.order.application.service;

import com.sophie.sophiemall.seckill.order.application.model.task.SeckillOrderTask;

public interface PlaceOrderTaskService {

    /**
     * 提交订单任务
     */
    boolean submitOrderTask(SeckillOrderTask seckillOrderTask);

}
