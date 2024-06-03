package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.portal.domain.OmsOrderReturnApplyParam;

/**
 * 订单退货管理Service
 */
public interface OmsMainOrderReturnApplyService {
    /**
     * 提交申请
     */
    int create(OmsOrderReturnApplyParam returnApply);
}
