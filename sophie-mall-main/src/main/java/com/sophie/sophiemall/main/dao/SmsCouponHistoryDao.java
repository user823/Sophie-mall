package com.sophie.sophiemall.main.dao;

import com.sophie.sophiemall.model.SmsCoupon;
import com.sophie.sophiemall.main.domain.SmsCouponHistoryDetail;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员优惠券领取历史自定义Dao
 */
public interface SmsCouponHistoryDao {
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId, @Param("useStatus")Integer useStatus);
}
