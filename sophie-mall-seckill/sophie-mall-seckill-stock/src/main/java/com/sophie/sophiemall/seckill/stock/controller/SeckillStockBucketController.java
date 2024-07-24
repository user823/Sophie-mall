package com.sophie.sophiemall.seckill.stock.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import com.sophie.sophiemall.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import com.sophie.sophiemall.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import com.sophie.sophiemall.seckill.stock.application.service.SeckillStockBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/stock/bucket")
public class SeckillStockBucketController {
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;

    /**
     * 库存分桶
     */
    @RequestMapping(value = "/arrangeStockBuckets", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> arrangeStockBuckets(@RequestHeader(SeckillConstants.USER_ID) Long userId, @RequestBody SeckillStockBucketWrapperCommand seckillStockCommond){
        seckillStockBucketService.arrangeStockBuckets(userId, seckillStockCommond);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取库存分桶数据
     */
    @RequestMapping(value = "/getTotalStockBuckets", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillStockBucketDTO> getTotalStockBuckets(@JsonFormat(shape = JsonFormat.Shape.STRING) Long goodsId, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillStockBucketService.getTotalStockBuckets(goodsId, version));
    }

}
