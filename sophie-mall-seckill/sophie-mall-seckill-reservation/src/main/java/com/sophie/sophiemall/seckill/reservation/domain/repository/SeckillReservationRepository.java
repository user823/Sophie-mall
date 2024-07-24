package com.sophie.sophiemall.seckill.reservation.domain.repository;

import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;

import java.util.List;

public interface SeckillReservationRepository {

    /**
     * 保存预约配置
     */
    boolean saveSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新预约配置
     */
    boolean updateSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新配置状态
     */
    boolean updateConfigStatus(Integer status, Long goodsId);

    /**
     * 更新当前预约人数
     */
    int updateReserveCurrentUserCount(Integer reserveCurrentUserCount, Long goodsId);

    /**
     * 获取配置列表
     */
    List<SeckillReservationConfig> getConfigList();

    /**
     * 获取配置详情
     */
    SeckillReservationConfig getConfigDetail(Long goodsId);

    /**
     * 根据商品id查看预约用户列表
     */
    List<SeckillReservationUser> getUserListByGoodsId(Long goodsId, Integer status);

    /**
     * 根据用户id查看预约的商品列表
     */
    List<SeckillReservationUser> getGoodsListByUserId(Long userId, Integer status);

    /**
     * 预约秒杀商品
     */
    boolean reserveGoods(SeckillReservationUser seckillReservationUser);

    /**
     * 取消预约秒杀商品
     */
    boolean cancelReserveGoods(Long goodsId, Long userId);


    /**
     * 获取用户预约的某个商品信息
     */
    SeckillReservationUser getSeckillReservationUser(Long userId, Long goodsId, Integer status);
}
