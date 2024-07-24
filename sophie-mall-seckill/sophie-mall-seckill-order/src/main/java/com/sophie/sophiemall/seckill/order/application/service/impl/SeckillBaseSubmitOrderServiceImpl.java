package com.sophie.sophiemall.seckill.order.application.service.impl;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.dubbo.goods.SeckillGoodsDubboService;
import com.sophie.sophiemall.seckill.dubbo.reservation.SeckillReservationDubboService;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.application.place.SeckillPlaceOrderService;
import com.sophie.sophiemall.seckill.order.application.security.SecurityService;
import com.sophie.sophiemall.seckill.order.application.service.SeckillSubmitOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {
    @Autowired
    private SecurityService securityService;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    protected SeckillPlaceOrderService seckillPlaceOrderService;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillReservationDubboService seckillReservationDubboService;

    @Override
    public void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (userId == null || seckillOrderCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        //模拟风控
        if (!securityService.securityPolicy(userId)){
            throw new SeckillException(ErrorCode.USER_INVALID);
        }
        //获取商品信息
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品信息
        seckillPlaceOrderService.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        //通过预约服务检测是否可以正常下单
        if (!seckillReservationDubboService.checkReservation(userId, seckillOrderCommand.getGoodsId())){
            throw new SeckillException(ErrorCode.GOODS_RESERVATION_NOT_RESERVE);
        }
    }
}