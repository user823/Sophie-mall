package com.sophie.sophiemall.main.service.impl;

import com.sophie.sophiemall.mapper.OmsOrderReturnApplyMapper;
import com.sophie.sophiemall.model.OmsOrderReturnApply;
import com.sophie.sophiemall.portal.domain.OmsOrderReturnApplyParam;
import com.sophie.sophiemall.main.service.OmsMainOrderReturnApplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单退货管理Service实现类
 */
@Service
public class OmsMainOrderReturnApplyServiceImpl implements OmsMainOrderReturnApplyService {
    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Override
    public int create(OmsOrderReturnApplyParam returnApply) {
        OmsOrderReturnApply realApply = new OmsOrderReturnApply();
        BeanUtils.copyProperties(returnApply,realApply);
        realApply.setCreateTime(new Date());
        realApply.setStatus(0);
        return returnApplyMapper.insert(realApply);
    }
}
