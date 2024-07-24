package com.sophie.sophiemall.seckill.user.controller;

import com.sophie.sophiemall.seckill.common.constants.SeckillConstants;
import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.model.dto.user.SeckillUserDTO;
import com.sophie.sophiemall.seckill.common.ratelimiter.qps.annotation.SeckillRateLimiter;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import com.sophie.sophiemall.seckill.user.application.service.SeckillUserService;
import com.sophie.sophiemall.seckill.user.domain.model.entity.SeckillUser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class SeckillUserController {

    private final Logger logger = LoggerFactory.getLogger(SeckillUserController.class);

    @Autowired
    private SeckillUserService seckillUserService;
    /**
     * 获取用户信息
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    @SeckillRateLimiter
    public ResponseMessage<SeckillUser> get(@RequestHeader(SeckillConstants.USER_ID) Long userId, @RequestParam String username){
        logger.info("SeckillUserController|获取到的userId|{}", userId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillUserService.getSeckillUserByUserName(username));
    }

    @RequestMapping(value = "/sleuth/filter/api", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> sleuthFilter(HttpServletRequest request) {
        Object traceIdObj = request.getAttribute("traceId");
        String traceId = traceIdObj == null ? "" : traceIdObj.toString();
        logger.info("获取到的traceId为: " + traceId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "sleuthFilter");
    }

    @RequestMapping(value = "/async/api", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> asyncApi() {
        logger.info("执行异步任务开始...");
        seckillUserService.asyncMethod();
        logger.info("异步任务执行结束...");
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "asyncApi");
    }

    @GetMapping(value = "/api1/demo1")
    public ResponseMessage<String> api1Demo1(){
        logger.info("访问了api1Demo1接口");
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "api1Demo1");
    }
    @GetMapping(value = "/api1/demo2")
    public ResponseMessage<String> api1Demo2(){
        logger.info("访问了api1Demo2接口");
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "api1Demo2");
    }

    @GetMapping(value = "/api2/demo1")
    public ResponseMessage<String> api2Demo1(){
        logger.info("访问了api2Demo1接口");
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "api2Demo1");
    }
    @GetMapping(value = "/api2/demo2")
    public ResponseMessage<String> api2Demo2(){
        logger.info("访问了api2Demo2接口");
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), "api2Demo2");
    }
}

