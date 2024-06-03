package com.sophie.sophiemall.main.service.impl;

import com.github.pagehelper.PageHelper;
import com.sophie.sophiemall.common.api.CommonPage;
import com.sophie.sophiemall.mapper.PmsBrandMapper;
import com.sophie.sophiemall.mapper.PmsProductMapper;
import com.sophie.sophiemall.model.PmsBrand;
import com.sophie.sophiemall.model.PmsProduct;
import com.sophie.sophiemall.model.PmsProductExample;
import com.sophie.sophiemall.main.dao.HomeDao;
import com.sophie.sophiemall.main.service.MainBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台品牌管理Service实现类
 */
@Service
public class MainBrandServiceImpl implements MainBrandService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> recommendList(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return homeDao.getRecommendBrandList(offset, pageSize);
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andDeleteStatusEqualTo(0)
                .andBrandIdEqualTo(brandId);
        List<PmsProduct> productList = productMapper.selectByExample(example);
        return CommonPage.restPage(productList);
    }
}
