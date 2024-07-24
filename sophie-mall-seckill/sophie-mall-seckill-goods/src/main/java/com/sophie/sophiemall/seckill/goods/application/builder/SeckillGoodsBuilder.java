package com.sophie.sophiemall.seckill.goods.application.builder;

import com.sophie.sophiemall.seckill.common.builder.SeckillCommonBuilder;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.utils.beans.BeanUtil;
import com.sophie.sophiemall.seckill.goods.application.command.SeckillGoodsCommand;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;

public class SeckillGoodsBuilder extends SeckillCommonBuilder {

    public static SeckillGoods toSeckillGoods(SeckillGoodsCommand seckillGoodsCommond){
        if (seckillGoodsCommond == null){
            return null;
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsCommond, seckillGoods);
        return seckillGoods;
    }

    public static SeckillGoodsDTO toSeckillGoodsDTO(SeckillGoods seckillGoods){
        if (seckillGoods == null){
            return null;
        }
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
        return seckillGoodsDTO;
    }
}