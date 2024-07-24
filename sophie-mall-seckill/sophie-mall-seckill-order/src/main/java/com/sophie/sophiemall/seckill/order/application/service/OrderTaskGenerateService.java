package com.sophie.sophiemall.seckill.order.application.service;

public interface OrderTaskGenerateService {

    /**
     * 生成下单标识
     * @param userId 用户id
     * @param goodsId 商品id
     * @return 下单标识
     */
    String generatePlaceOrderTaskId(Long userId, Long goodsId);
}