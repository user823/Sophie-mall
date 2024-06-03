package com.sophie.sophiemall.service;

import com.sophie.sophiemall.model.OmsCompanyAddress;

import java.util.List;

/**
 * 收货地址理Service
 */
public interface OmsCompanyAddressService {
    /**
     * 获取全部收货地址
     */
    List<OmsCompanyAddress> list();
}
