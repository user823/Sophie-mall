package com.sophie.sophiemall.seckill.goods.application.service;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.model.message.TxMessage;
import com.sophie.sophiemall.seckill.goods.application.command.SeckillGoodsCommand;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;
public interface SeckillGoodsService {

    /**
     * 保存商品信息
     */
    void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommond);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsId(Long id);

    /**
     * 根据id获取商品详细信息（带缓存）
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    /**
     * 根据活动id从缓存中获取数据
     */
    List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version);

    /**
     * 修改商品状态
     */
    void updateStatus(Integer status, Long id);

    /**
     * 扣减库存
     */
    boolean updateAvailableStock(Integer count, Long id);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

    /**
     * 扣减库存
     */
    boolean updateAvailableStock(TxMessage txMessage);

    /**
     * 获取商品可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
