package com.sophie.sophiemall.seckill.goods.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.model.dto.goods.SeckillGoodsDTO;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import com.sophie.sophiemall.seckill.goods.application.command.SeckillGoodsCommand;
import com.sophie.sophiemall.seckill.goods.application.service.SeckillGoodsService;
import com.sophie.sophiemall.seckill.goods.domain.model.entity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class SeckillGoodsController /*extends BaseController*/ {

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    /**
     * 保存秒杀商品
     */
    @RequestMapping(value = "/saveSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivityDTO(SeckillGoodsCommand seckillGoodsCommond){
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommond);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(@JsonFormat(shape = JsonFormat.Shape.STRING) Long id){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsId(id));
    }

    /**
     * 获取商品详情（带缓存）
     */
    @RequestMapping(value = "/getSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    //@SentinelResource(value = "QUEUE-DATA-FLOW")
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(@JsonFormat(shape = JsonFormat.Shape.STRING) Long id, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsByActivityId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(@JsonFormat(shape = JsonFormat.Shape.STRING) Long activityId){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }
    /**
     * 获取商品列表(带缓存)
     */
    @RequestMapping(value = "/getSeckillGoodsList", method = {RequestMethod.GET,RequestMethod.POST})
    @SentinelResource(value = "QUEUE-DATA-DEGRADE")
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsByActivityId(@JsonFormat(shape = JsonFormat.Shape.STRING) Long activityId, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 更新商品状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(Integer status, @JsonFormat(shape = JsonFormat.Shape.STRING) Long id){
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }
}
