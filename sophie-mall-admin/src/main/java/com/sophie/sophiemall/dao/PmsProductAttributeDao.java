package com.sophie.sophiemall.dao;

import com.sophie.sophiemall.dto.ProductAttrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义商品属性Dao
 */
public interface PmsProductAttributeDao {
    /**
     * 获取商品属性信息
     */
    List<ProductAttrInfo> getProductAttrInfo(@Param("id") Long productCategoryId);
}
