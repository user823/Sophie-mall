package com.sophie.sophiemall.seckill.reservation.application.service;

import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationConfigCommand;
import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationUserCommand;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;

import java.util.List;

public interface SeckillReservationService {

    /**
     * 保存预约配置
     */
    boolean saveSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand);

    /**
     * 更新预约配置
     */
    boolean updateSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand);

    /**
     * 更新配置状态
     */
    boolean updateConfigStatus(Integer status, Long goodsId);

    /**
     * 获取配置列表
     */
    List<SeckillReservationConfig> getConfigList(Long version);

    /**
     * 获取配置详情
     */
    SeckillReservationConfig getConfigDetail(Long goodsId, Long version);

    /**
     * 根据商品id查看预约用户列表
     */
    List<SeckillReservationUser> getUserListByGoodsId(Long goodsId, Long version);

    /**
     * 根据用户id查看预约的商品列表
     */
    List<SeckillReservationUser> getGoodsListByUserId(Long userId, Long version);

    /**
     * 预约秒杀商品
     */
    boolean reserveGoods(SeckillReservationUserCommand seckillReservationUserCommand);

    /**
     * 取消预约秒杀商品
     */
    boolean cancelReserveGoods(SeckillReservationUserCommand seckillReservationUserCommand);

    /**
     * 获取用户预约的某个商品信息
     */
    SeckillReservationUser getSeckillReservationUser(SeckillReservationUserCommand seckillReservationUserCommand);
}
