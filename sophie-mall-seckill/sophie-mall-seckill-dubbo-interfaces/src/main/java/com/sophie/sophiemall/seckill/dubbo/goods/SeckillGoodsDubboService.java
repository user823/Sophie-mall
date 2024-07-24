package com.sophie.sophiemall.seckill.dubbo.goods;

import com.sophie.sophiemall.seckill.common.cache.model.SeckillBusinessCache;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;

public interface SeckillGoodsDubboService {

    /**
     * 根据id和版本号获取商品详情
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer count, Long id);

    /**
     * 扣减商品库存
     */
    boolean updateAvailableStock(Integer count, Long id);

    /**
     * 根据商品id获取可用库存
     */
    Integer getAvailableStockById(Long goodsId);

    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
