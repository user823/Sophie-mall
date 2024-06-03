package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.common.api.CommonPage;
import com.sophie.sophiemall.model.PmsBrand;
import com.sophie.sophiemall.model.PmsProduct;

import java.util.List;

/**
 * 前台品牌管理Service
 */
public interface MainBrandService {
    /**
     * 分页获取推荐品牌
     */
    List<PmsBrand> recommendList(Integer pageNum, Integer pageSize);

    /**
     * 获取品牌详情
     */
    PmsBrand detail(Long brandId);

    /**
     * 分页获取品牌关联商品
     */
    CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize);
}
