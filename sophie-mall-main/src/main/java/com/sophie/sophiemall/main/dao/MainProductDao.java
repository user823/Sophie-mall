package com.sophie.sophiemall.main.dao;

import com.sophie.sophiemall.model.SmsCoupon;
import com.sophie.sophiemall.main.domain.CartProduct;
import com.sophie.sophiemall.main.domain.PromotionProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台系统自定义商品Dao
 */
public interface MainProductDao {
    CartProduct getCartProduct(@Param("id") Long id);
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);
    List<SmsCoupon> getAvailableCouponList(@Param("productId") Long productId,@Param("productCategoryId")Long productCategoryId);
}
