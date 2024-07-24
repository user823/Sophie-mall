package com.sophie.sophiemall.seckill.activity.application.builder;

import com.sophie.sophiemall.seckill.activity.application.command.SeckillActivityCommand;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.common.builder.SeckillCommonBuilder;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;

public class SeckillActivityBuilder extends SeckillCommonBuilder {

    public static SeckillActivity toSeckillActivity(SeckillActivityCommand seckillActivityCommand){
        if (seckillActivityCommand == null){
            return null;
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityCommand, seckillActivity);
        return seckillActivity;
    }

    public static SeckillActivityDTO toSeckillActivityDTO(SeckillActivity seckillActivity){
        if (seckillActivity == null){
            return null;
        }
        SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
        BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
        return seckillActivityDTO;
    }
}
