package com.sophie.sophiemall.seckill.user.controller;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "/nacos")
public class NacosController {

    private final Logger logger = LoggerFactory.getLogger(NacosController.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Value("${seckill.author.name}")
    private String nacosAuthorName;

    @GetMapping("/test")
    public ResponseMessage<String> nacosTest(){
        String authorName = context.getEnvironment().getProperty("seckill.author.name");
        logger.info("获取到的作者姓名为：{}", authorName);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), authorName);
    }

    @GetMapping("/name")
    public ResponseMessage<String> nacosName(){
        logger.info("从Nacos中获取到的作者的姓名为：{}", nacosAuthorName);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), nacosAuthorName);
    }
}