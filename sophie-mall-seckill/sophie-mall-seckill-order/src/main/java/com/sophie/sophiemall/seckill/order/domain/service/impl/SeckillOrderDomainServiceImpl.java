package com.sophie.sophiemall.seckill.order.domain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillOrderStatus;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.order.domain.event.SeckillOrderEvent;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;
import com.sophie.sophiemall.seckill.order.domain.repository.SeckillOrderRepository;
import com.sophie.sophiemall.seckill.order.domain.service.SeckillOrderDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;
    @Autowired
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("saveSeckillOrder|下单|{}", JSON.toJSONString(seckillOrder));
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        boolean saveSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if (saveSuccess){
            logger.info("saveSeckillOrder|创建订单成功|{}", JSON.toJSONString(seckillOrder));
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(seckillOrder.getId(), SeckillOrderStatus.CREATED.getCode(), getTopicEvent());
            messageSenderService.send(seckillOrderEvent);
        }
        return saveSuccess;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId) {
        if (goodsId == null){
            throw  new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByGoodsId(goodsId);
    }

    @Override
    public void deleteOrderShardingUserId(Long orderId, Long userId) {
        if (orderId == null || userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrderRepository.deleteOrderShardingUserId(orderId, userId);
    }

    @Override
    public void deleteOrderShardingGoodsId(Long orderId, Long goodsId) {
        if (orderId == null || goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrderRepository.deleteOrderShardingGoodsId(orderId, goodsId);
    }


    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
