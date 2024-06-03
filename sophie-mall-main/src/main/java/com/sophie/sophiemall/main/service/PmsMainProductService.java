package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.model.PmsProduct;
import com.sophie.sophiemall.main.domain.PmsMainProductDetail;
import com.sophie.sophiemall.main.domain.PmsProductCategoryNode;

import java.util.List;

/**
 * 前台商品管理Service
 */
public interface PmsMainProductService {
    /**
     * 综合搜索商品
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort);

    /**
     * 以树形结构获取所有商品分类
     */
    List<PmsProductCategoryNode> categoryTreeList();

    /**
     * 获取前台商品详情
     */
    PmsMainProductDetail detail(Long id);
}
