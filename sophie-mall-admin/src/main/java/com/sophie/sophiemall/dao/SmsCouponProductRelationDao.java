package com.sophie.sophiemall.dao;

import com.sophie.sophiemall.model.SmsCouponProductRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义优惠券和商品关系关系Dao
 */
public interface SmsCouponProductRelationDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list")List<SmsCouponProductRelation> productRelationList);
}
