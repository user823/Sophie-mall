package com.sophie.sophiemall.service.impl;

import com.sophie.sophiemall.mapper.OmsCompanyAddressMapper;
import com.sophie.sophiemall.model.OmsCompanyAddress;
import com.sophie.sophiemall.model.OmsCompanyAddressExample;
import com.sophie.sophiemall.service.OmsCompanyAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址管理Service实现类
 */
@Service
public class OmsCompanyAddressServiceImpl implements OmsCompanyAddressService {
    @Autowired
    private OmsCompanyAddressMapper companyAddressMapper;
    @Override
    public List<OmsCompanyAddress> list() {
        return companyAddressMapper.selectByExample(new OmsCompanyAddressExample());
    }
}
