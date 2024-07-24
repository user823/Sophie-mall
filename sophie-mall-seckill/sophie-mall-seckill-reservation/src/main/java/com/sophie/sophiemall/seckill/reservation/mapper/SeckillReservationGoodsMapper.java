package com.sophie.sophiemall.seckill.reservation.mapper;

import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeckillReservationGoodsMapper {

    /**
     * 根据商品id查看预约用户列表
     */
    List<SeckillReservationUser> getUserListByGoodsId(@Param("goodsId") Long goodsId, @Param("status") Integer status);

    /**
     * 预约秒杀商品
     */
    int reserveGoods(SeckillReservationUser seckillReservationUser);

    /**
     * 取消预约秒杀商品
     */
    int cancelReserveGoods(@Param("goodsId") Long goodsId, @Param("userId") Long userId);
}
