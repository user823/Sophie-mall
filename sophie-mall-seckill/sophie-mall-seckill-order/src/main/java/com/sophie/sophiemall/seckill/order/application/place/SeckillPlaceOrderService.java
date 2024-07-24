package com.sophie.sophiemall.seckill.order.application.place;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillOrderStatus;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.TransactionExecutor;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.common.utils.id.SnowFlakeFactory;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;

import java.math.BigDecimal;
import java.util.Date;

public interface SeckillPlaceOrderService extends TransactionExecutor {

    /**
     * 下单操作
     */
    Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 本地事务执行保存订单操作
     */
    boolean saveOrderInTransaction(TxMessage txMessage);

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setUserId(userId);
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        BigDecimal orderPrice = seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(TxMessage txMessage){
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(txMessage.getTxNo());
        seckillOrder.setUserId(txMessage.getUserId());
        seckillOrder.setGoodsId(txMessage.getGoodsId());
        seckillOrder.setGoodsName(txMessage.getGoodsName());
        seckillOrder.setActivityPrice(txMessage.getActivityPrice());
        seckillOrder.setQuantity(txMessage.getQuantity());
        BigDecimal orderPrice = txMessage.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setActivityId(txMessage.getActivityId());
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 检测商品信息
     */
    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        //商品不存在
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        //已经超出活动时间范围
        if (!seckillGoods.isInSeckilling()){
            throw new SeckillException(ErrorCode.BEYOND_TIME);
        }
        //商品未上线
        if (!seckillGoods.isOnline()){
            throw new SeckillException(ErrorCode.GOODS_PUBLISH);
        }
        //商品已下架
        if (seckillGoods.isOffline()){
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }
        //触发限购
        if (seckillGoods.getLimitNum() < seckillOrderCommand.getQuantity()){
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null || seckillGoods.getAvailableStock() <= 0 || seckillOrderCommand.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

    /**
     * 事务消息
     */
    default TxMessage getTxMessage(String destination, Long txNo, Long userId, String placeOrderType, Boolean exception,
                                   SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods, Integer bucketSerialNo, String orderTaskId){
        //构建事务消息
        return new TxMessage(destination, txNo, seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity(),
                seckillOrderCommand.getActivityId(), seckillOrderCommand.getVersion(), userId, seckillGoods.getGoodsName(),
                seckillGoods.getActivityPrice(), placeOrderType, exception, bucketSerialNo, orderTaskId);
    }
}
