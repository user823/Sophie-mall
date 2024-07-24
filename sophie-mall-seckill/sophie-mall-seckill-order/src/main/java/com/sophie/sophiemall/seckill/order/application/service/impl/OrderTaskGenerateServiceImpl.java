package com.sophie.sophiemall.seckill.order.application.service.impl;

import com.sophie.sophiemall.seckill.order.application.service.OrderTaskGenerateService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class OrderTaskGenerateServiceImpl implements OrderTaskGenerateService {
    @Override
    public String generatePlaceOrderTaskId(Long userId, Long goodsId) {
        String toEncrypt = userId + "_" + goodsId;
        return DigestUtils.md5DigestAsHex(toEncrypt.getBytes());
    }
}