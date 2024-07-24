package com.sophie.sophiemall.seckill.activity.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophie.sophiemall.seckill.activity.application.command.SeckillActivityCommand;
import com.sophie.sophiemall.seckill.activity.application.service.SeckillActivityService;
import com.sophie.sophiemall.seckill.activity.domain.model.entity.SeckillActivity;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.model.dto.activity.SeckillActivityDTO;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import com.sophie.sophiemall.seckill.common.utils.date.JodaDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class SeckillActivityController {
    @Autowired
    private SeckillActivityService seckillActivityService;
    /**
     * 保存秒杀活动
     */
    @RequestMapping(value = "/saveSeckillActivity", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivityDTO(SeckillActivityCommand seckillActivityCommand){
        seckillActivityService.saveSeckillActivity(seckillActivityCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/getSeckillActivityList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status));
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/getSeckillActivityListBetweenStartTimeAndEndTime", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityListBetweenStartTimeAndEndTime(@RequestParam(value = "currentTime", required = false) String currentTime,
                                                                                                   @RequestParam(value = "status", required = false)Integer status){
        List<SeckillActivity> seckillActivityList = seckillActivityService.getSeckillActivityListBetweenStartTimeAndEndTime(JodaDateTimeUtils.parseStringToDate(currentTime, JodaDateTimeUtils.DATE_TIME_FORMAT), status);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityList);
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/seckillActivityList", method = {RequestMethod.GET,RequestMethod.POST})
    //@SentinelResource(value = "QUEUE-DATA-FLOW")
    public ResponseMessage<List<SeckillActivityDTO>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status,
                                                                            @RequestParam(value = "version", required = false) Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status, version));
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/seckillActivityListBetweenStartTimeAndEndTime", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivityDTO>> getSeckillActivityListBetweenStartTimeAndEndTime(@RequestParam(value = "currentTime", required = false) String currentTime,
                                                                                                      @RequestParam(value = "status", required = false) Integer status,
                                                                                                      @RequestParam(value = "version", required = false) Long version){
        List<SeckillActivityDTO> seckillActivityList = seckillActivityService.getSeckillActivityListBetweenStartTimeAndEndTime(JodaDateTimeUtils.parseStringToDate(currentTime, JodaDateTimeUtils.DATE_TIME_FORMAT), status, version);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityList);
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @RequestMapping(value = "/seckillActivity", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillActivityDTO> getSeckillActivityById(@RequestParam(value = "id", required = false) @JsonFormat(shape = JsonFormat.Shape.STRING) Long id,
                                                                      @RequestParam(value = "version", required = false) Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivity(id, version));
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @RequestMapping(value = "/getSeckillActivityById", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillActivity> getSeckillActivityById(@RequestParam(value = "id", required = false) @JsonFormat(shape = JsonFormat.Shape.STRING) Long id){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityById(id));
    }

    /**
     * 更新活动的状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(@RequestParam(value = "status", required = false) Integer status,
                                                @RequestParam(value = "id", required = false) @JsonFormat(shape = JsonFormat.Shape.STRING) Long id){
        seckillActivityService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }
}
