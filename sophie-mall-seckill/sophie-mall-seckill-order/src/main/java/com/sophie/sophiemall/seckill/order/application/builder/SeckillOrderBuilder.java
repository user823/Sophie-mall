package com.sophie.sophiemall.seckill.order.application.builder;

import com.sophie.sophiemall.seckill.common.builder.SeckillCommonBuilder;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;

public class SeckillOrderBuilder extends SeckillCommonBuilder {

    public static SeckillOrder toSeckillOrder(SeckillOrderCommand seckillOrderCommand){
        if (seckillOrderCommand == null){
            return null;
        }
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        return seckillOrder;
    }

}