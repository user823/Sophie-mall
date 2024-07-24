package com.sophie.sophiemall.seckill.reservation.application.builder;

import com.sophie.sophiemall.seckill.common.builder.SeckillCommonBuilder;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.reservation.application.command.SeckillReservationConfigCommand;
import com.sophie.sophiemall.seckill.reservation.domain.model.entity.SeckillReservationConfig;

public class SeckillReservationConfigBuilder extends SeckillCommonBuilder {

    public static SeckillReservationConfig toSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand){
        if (seckillReservationConfigCommand == null || seckillReservationConfigCommand.isEmpty()){
            return null;
        }
        SeckillReservationConfig seckillReservationConfig = new SeckillReservationConfig();
        BeanUtil.copyProperties(seckillReservationConfigCommand, seckillReservationConfig);
        return seckillReservationConfig;
    }
}
