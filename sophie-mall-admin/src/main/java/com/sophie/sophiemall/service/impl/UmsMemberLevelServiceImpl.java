package com.sophie.sophiemall.service.impl;

import com.sophie.sophiemall.mapper.UmsMemberLevelMapper;
import com.sophie.sophiemall.model.UmsMemberLevel;
import com.sophie.sophiemall.model.UmsMemberLevelExample;
import com.sophie.sophiemall.service.UmsMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员等级管理Service实现类
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService{
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Override
    public List<UmsMemberLevel> list(Integer defaultStatus) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andDefaultStatusEqualTo(defaultStatus);
        return memberLevelMapper.selectByExample(example);
    }
}
