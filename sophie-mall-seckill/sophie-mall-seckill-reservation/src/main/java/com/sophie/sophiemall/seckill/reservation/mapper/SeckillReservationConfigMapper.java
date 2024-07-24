package com.sophie.sophiemall.seckill.reservation.mapper;

import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeckillReservationConfigMapper {

    /**
     * 保存预约配置
     */
    int saveSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新预约配置
     */
    int updateSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新状态
     */
    int updateStatus(@Param("status") Integer status, @Param("goodsId") Long goodsId);

    /**
     * 更新当前预约人数
     */
    int updateReserveCurrentUserCount(@Param("reserveCurrentUserCount") Integer reserveCurrentUserCount, @Param("goodsId") Long goodsId);

    /**
     * 获取配置列表
     */
    List<SeckillReservationConfig> getConfigList();

    /**
     * 获取配置详情
     */
    SeckillReservationConfig getConfigDetail(@Param("goodsId") Long goodsId);
}
