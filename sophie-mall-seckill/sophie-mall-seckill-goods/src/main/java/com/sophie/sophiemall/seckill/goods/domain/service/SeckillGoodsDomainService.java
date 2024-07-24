package com.sophie.sophiemall.seckill.goods.domain.service;

import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

public interface SeckillGoodsDomainService {


    /**
     * 保存商品信息
     */
    boolean saveSeckillGoods(SeckillGoods seckillGoods);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsId(Long id);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

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
     * 回滚新增库存
     */
    boolean incrementAvailableStock(Integer count, Long id);


    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);
}
