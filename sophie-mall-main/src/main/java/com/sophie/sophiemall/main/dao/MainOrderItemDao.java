package com.sophie.sophiemall.main.dao;

import com.sophie.sophiemall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品信息自定义Dao
 */
public interface MainOrderItemDao {
    int insertList(@Param("list") List<OmsOrderItem> list);
}
