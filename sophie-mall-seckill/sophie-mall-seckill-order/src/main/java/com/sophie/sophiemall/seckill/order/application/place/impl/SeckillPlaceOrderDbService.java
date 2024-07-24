package com.sophie.sophiemall.seckill.order.application.place.impl;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.sophie.sophiemall.seckill.common.cache.distribute.DistributedCacheService;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.exception.SeckillException;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.common.mq.MessageSenderService;
import com.sophie.sophiemall.seckill.common.mq.TransactionExecutor;
import com.sophie.sophiemall.seckill.common.mq.TransactionResult;
import com.sophie.sophiemall.seckill.common.utils.id.SnowFlakeFactory;
import com.sophie.sophiemall.seckill.dubbo.goods.SeckillGoodsDubboService;
import com.sophie.sophiemall.seckill.order.application.model.command.SeckillOrderCommand;
import com.sophie.sophiemall.seckill.order.application.place.SeckillPlaceOrderService;
import com.sophie.sophiemall.seckill.order.domain.model.entity.SeckillOrder;
import com.sophie.sophiemall.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDbService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderDbService.class);
    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (userId == null || seckillOrderCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        //获取商品
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品信息
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        boolean exception = false;
        try{
            //获取商品库存
            Integer availableStock = seckillGoodsDubboService.getAvailableStockById(seckillOrderCommand.getGoodsId());
            //库存不足
            if (availableStock == null || availableStock < seckillOrderCommand.getQuantity()){
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
        }catch (Exception e){
            exception = true;
            logger.error("SeckillPlaceOrderDbService|下单异常|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());

            // 其实可以统一返回库存不足
            if (e instanceof SeckillException){
                throw e;
            }else {
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
        }
        //事务消息
        //发送事务消息
        TransactionResult sendResult = messageSenderService.sendMessageInTransaction(this.getTxMessage(SeckillConstants.TOPIC_TX_MSG, txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_DB, exception, seckillOrderCommand, seckillGoods, 0, seckillOrderCommand.getOrderTaskId()), this);
        if (sendResult.getStatus() != TransactionResult.TransactionStatus.COMMIT){
            logger.error("SeckillPlaceOrderDbService|发送事务消息失败|参数:{}", JSONObject.toJSONString(seckillOrderCommand));
        }
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrderInTransaction(TxMessage txMessage) {
        boolean executeResult = false;
        try{
            Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            if (BooleanUtil.isTrue(submitTransaction)){
                logger.info("saveOrderInTransaction|已经执行过本地事务|{}", txMessage.getTxNo());
                return true;
            }
            //构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(txMessage);
            //保存订单
            executeResult = seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            //保存事务日志
            if (executeResult){
                distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())), txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
            }
        }catch (Exception e){
            logger.error("saveOrderInTransaction|异常|{}", e.getMessage());
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            throw e;
        }
        return executeResult;
    }

    @Override
    public TransactionResult executeLocalTransaction(TxMessage txMessage) {
        try {
            // 已经抛出异常，执行回滚
            if (BooleanUtil.isTrue(txMessage.getException())) {
                return new TransactionResult(TransactionResult.TransactionStatus.ROLLBACK);
            }
            boolean executeResult = saveOrderInTransaction(txMessage);
            if (executeResult) {
                logger.info("executeLocalTransaction|秒杀订单微服务提交本地事务成功|{}", txMessage.getTxNo());
                return new TransactionResult(TransactionResult.TransactionStatus.COMMIT);
            }
            logger.info("executeLocalTransaction|秒杀订单微服务提交本地事务失败|{}", txMessage.getTxNo());
            return new TransactionResult(TransactionResult.TransactionStatus.ROLLBACK);
        } catch(Exception e) {
            logger.error("executeLocalTransaction|秒杀订单微服务异常回滚事务|{}",txMessage.getTxNo());
            return new TransactionResult(TransactionResult.TransactionStatus.ROLLBACK);
        }
    }

    @Override
    public TransactionResult checkLocalTransaction(TxMessage txMessage) {
        logger.info("checkLocalTransaction|秒杀订单微服务查询本地事务|{}", txMessage.getTxNo());
        Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
        return BooleanUtil.isTrue(submitTransaction) ? new TransactionResult(TransactionResult.TransactionStatus.COMMIT) : new TransactionResult(TransactionResult.TransactionStatus.ROLLBACK);
    }
}
