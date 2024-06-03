package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.model.OmsCartItem;
import com.sophie.sophiemall.main.domain.CartPromotionItem;

import java.util.List;

/**
 * 促销管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
