package com.sophie.sophiemall.seckill.dubbo.reservation;

public interface SeckillReservationDubboService {

    /**
     * 下单时校验预约信息
     * 如果当前商品不存在预约配置，则说明当前商品无需提前预约即可下单，返回true
     * 如果当前商品存在预约配置，则校验当前用户是否预约过商品，如果预约过当前商品，则返回true，否则返回false。
     */
    boolean checkReservation(Long userId, Long goodsId);
}
