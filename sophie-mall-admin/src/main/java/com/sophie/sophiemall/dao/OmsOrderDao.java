package com.sophie.sophiemall.dao;

import com.sophie.sophiemall.dto.OmsOrderDeliveryParam;
import com.sophie.sophiemall.dto.OmsOrderDetail;
import com.sophie.sophiemall.dto.OmsOrderQueryParam;
import com.sophie.sophiemall.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单自定义查询Dao
 */
public interface OmsOrderDao {
    /**
     * 条件查询订单
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("id") Long id);
}
