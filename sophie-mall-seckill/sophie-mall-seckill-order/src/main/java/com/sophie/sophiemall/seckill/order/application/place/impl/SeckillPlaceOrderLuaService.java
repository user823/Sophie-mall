package com.sophie.sophiemall.seckill.order.application.place.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
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
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLuaService.class);
    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        boolean exception = false;
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        Long decrementResult = 0L;
        try{
            //获取商品限购信息
            Object limitObj = distributedCacheService.getObject(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId())));
            //如果从Redis获取到的限购信息为null，则说明商品已经下线
            if (limitObj == null){
                throw new SeckillException(ErrorCode.GOODS_OFFLINE);
            }

            if (Integer.parseInt(String.valueOf(limitObj)) < seckillOrderCommand.getQuantity()){
                throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
            }
            decrementResult = distributedCacheService.decrementByLua(key, seckillOrderCommand.getQuantity());
            this.checkResult(decrementResult);
        }catch (Exception e){
            logger.error("SeckillPlaceOrderLuaService|下单异常|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            exception = true;
            if (decrementResult == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS){
                //将内存中的库存增加回去
                distributedCacheService.incrementByLua(key, seckillOrderCommand.getQuantity());
            }

            // 其实可以统一返回库存不足
            if (e instanceof SeckillException){
                throw e;
            }else {
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
        }
        //发送事务消息
        TransactionResult sendResult = messageSenderService.sendMessageInTransaction(this.getTxMessage(SeckillConstants.TOPIC_TX_MSG, txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_LUA, exception, seckillOrderCommand, seckillGoods, 0, seckillOrderCommand.getOrderTaskId()), this);
        //发送消息失败
        if (sendResult.getStatus() != TransactionResult.TransactionStatus.COMMIT){
            //未抛出异常
            if (!exception && decrementResult == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS){
                //将内存中的库存增加回去
                distributedCacheService.incrementByLua(key, seckillOrderCommand.getQuantity());
            }
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
            this.rollbackCacheStack(txMessage);
            throw e;
        }
        return executeResult;
    }

    /**
     * 回滚缓存库存
     */
    private void rollbackCacheStack(TxMessage txMessage) {
        //扣减过缓存库存
        if (BooleanUtil.isFalse(txMessage.getException())){
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            //已经执行过恢复缓存库存的方法
            if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(txMessage));
                return;
            }
            //只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(txMessage.getGoodsId()));
            distributedCacheService.increment(key, txMessage.getQuantity());
        }
    }
    private void checkResult(Long result){
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
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
