package com.sophie.sophiemall.service.impl;

import com.sophie.sophiemall.mapper.CmsPrefrenceAreaMapper;
import com.sophie.sophiemall.model.CmsPrefrenceArea;
import com.sophie.sophiemall.model.CmsPrefrenceAreaExample;
import com.sophie.sophiemall.service.CmsPrefrenceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品优选管理Service实现类
 */
@Service
public class CmsPrefrenceAreaServiceImpl implements CmsPrefrenceAreaService {
    @Autowired
    private CmsPrefrenceAreaMapper prefrenceAreaMapper;

    @Override
    public List<CmsPrefrenceArea> listAll() {
        return prefrenceAreaMapper.selectByExample(new CmsPrefrenceAreaExample());
    }
}
