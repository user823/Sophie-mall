package com.sophie.sophiemall.seckill.order.application.service.impl;

import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.order.SeckillOrderSubmitDTO;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.application.model.task.SeckillOrderTask;
import com.sophie.sophiemall.seckill.order.application.service.OrderTaskGenerateService;
import com.sophie.sophiemall.seckill.order.application.service.PlaceOrderTaskService;
import com.sophie.sophiemall.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
public class SeckillAsyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {
    @Autowired
    private OrderTaskGenerateService orderTaskGenerateService;
    @Autowired
    private PlaceOrderTaskService placeOrderTaskService;
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        //进行基本的检查
        this.checkSeckillOrder(userId, seckillOrderCommand);
        //生成订单任务id
        String orderTaskId = orderTaskGenerateService.generatePlaceOrderTaskId(userId, seckillOrderCommand.getGoodsId());
        //将taskId存入seckillOrderCommand
        seckillOrderCommand.setOrderTaskId(orderTaskId);
        //构造下单任务
        SeckillOrderTask seckillOrderTask = new SeckillOrderTask(SeckillConstants.TOPIC_ORDER_MSG, orderTaskId, userId, seckillOrderCommand);
        //提交订单
        boolean isSubmit = placeOrderTaskService.submitOrderTask(seckillOrderTask);
        //提交失败
        if (!isSubmit){
            throw new SeckillException(ErrorCode.ORDER_FAILED);
        }
        return new SeckillOrderSubmitDTO(orderTaskId, seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_TASK);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask) {
        Long orderId = seckillPlaceOrderService.placeOrder(seckillOrderTask.getUserId(), seckillOrderTask.getSeckillOrderCommand());
        if (orderId != null){
            String key = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ORDER_ID_KEY, seckillOrderTask.getOrderTaskId());
            distributedCacheService.put(key, orderId, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }
}
