package com.sophie.sophiemall.seckill.reservation.application.builder;

import com.sophie.sophiemall.seckill.common.builder.SeckillCommonBuilder;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationUserCommand;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationUser;

public class SeckillReservationUserBuilder extends SeckillCommonBuilder {

    public static SeckillReservationUser toSeckillReservationUser(SeckillReservationUserCommand seckillReservationUserCommand){
        if (seckillReservationUserCommand == null || seckillReservationUserCommand.isEmpty()){
            return null;
        }
        SeckillReservationUser seckillReservationUser = new SeckillReservationUser();
        BeanUtil.copyProperties(seckillReservationUserCommand, seckillReservationUser);
        return seckillReservationUser;
    }
}
