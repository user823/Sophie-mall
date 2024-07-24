package com.sophie.sophiemall.seckill.goods.domain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillGoodsStatus;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.goods.domain.event.SeckillGoodsEvent;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;
import com.sophie.sophiemall.seckill.goods.domain.repository.SeckillGoodsRepository;
import com.sophie.sophiemall.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsDomainServiceImpl implements SeckillGoodsDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsDomainServiceImpl.class);

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    @Autowired
    private MessageSenderService messageSenderService;

    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public boolean saveSeckillGoods(SeckillGoods seckillGoods) {
        logger.info("goodsPublish|发布秒杀商品|{}", JSON.toJSON(seckillGoods));
        if (seckillGoods == null || !seckillGoods.validateParams()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        boolean success = seckillGoodsRepository.saveSeckillGoods(seckillGoods) == 1;
        if (success){
            logger.info("goodsPublish|秒杀商品已经发布|{}", seckillGoods.getId());
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getActivityId(), SeckillGoodsStatus.PUBLISHED.getCode(), this.getTopicEvent());
            messageSenderService.send(seckillGoodsEvent);
            logger.info("goodsPublish|秒杀商品事件已经发布|{}", seckillGoods.getId());
        }
        return success;
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsRepository.getSeckillGoodsId(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        logger.info("goodsPublish|更新秒杀商品状态|{}", id);
        if (id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsId(id);
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        //更新状态状态
        seckillGoodsRepository.updateStatus(status, id);
        logger.info("goodsPublish|秒杀商品状态已经更新|{},{}", id, status);

        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getActivityId(), status, this.getTopicEvent());
        messageSenderService.send(seckillGoodsEvent);
        logger.info("goodsPublish|秒杀商品事件已经发布|{}", seckillGoodsEvent.getId());
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        if (count == null || count <= 0 || id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsId(id);
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        boolean isUpdate = seckillGoodsRepository.updateAvailableStock(count, id) > 0;
        if (isUpdate){
            logger.info("goodsPublish|秒杀商品库存已经更新|{}", id);
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getActivityId(), seckillGoods.getStatus(), getTopicEvent());
            messageSenderService.send(seckillGoodsEvent);
            logger.info("goodsPublish|秒杀商品库存事件已经发布|{}", id);
        }else {
            logger.info("goodsPublish|秒杀商品库存未更新|{}", id);
        }
        return isUpdate;
    }

    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        logger.info("goodsPublish|更新秒杀商品库存|{}", id);
        if (count == null || count <= 0 || id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsRepository.updateAvailableStock(count, id) > 0;
    }

    @Override
    public boolean incrementAvailableStock(Integer count, Long id) {
        if (count == null || count <= 0 || id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsId(id);
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        boolean isUpdate = seckillGoodsRepository.incrementAvailableStock(count, id) > 0;
        if (isUpdate){
            logger.info("goodsPublish|秒杀商品库存已经回滚|{}", id);
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getActivityId(), seckillGoods.getStatus(), getTopicEvent());
            messageSenderService.send(seckillGoodsEvent);
            logger.info("goodsPublish|秒杀商品库存事件已经发布|{}", id);
        }else {
            logger.info("goodsPublish|秒杀商品库存未更新|{}", id);
        }
        return isUpdate;
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
