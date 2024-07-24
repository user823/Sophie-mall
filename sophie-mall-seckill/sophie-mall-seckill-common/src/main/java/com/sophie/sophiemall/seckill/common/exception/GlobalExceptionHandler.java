package com.sophie.sophiemall.seckill.common.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.sophie.sophiemall.seckill.common.response.ResponseMessage;
import com.sophie.sophiemall.seckill.common.response.ResponseMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(SeckillException.class)
    public ResponseMessage<String> handleSeckillException(SeckillException e) {
        logger.error("服务器抛出了异常：{}", e);
        return ResponseMessageBuilder.build(e.getCode(), e.getMessage());
    }

    /**
     * Sentinel规则
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseMessage<String> handleUndeclaredThrowableException(UndeclaredThrowableException e) {
        ErrorCode errorCode = null;
        if (e.getUndeclaredThrowable() instanceof FlowException){
            errorCode = ErrorCode.SENTINEL_FLOW;
        }else if (e.getUndeclaredThrowable() instanceof DegradeException){
            errorCode = ErrorCode.SENTINEL_DEGRADE;
        }else if (e.getUndeclaredThrowable() instanceof ParamFlowException){
            errorCode = ErrorCode.SENTINEL_PARAMAS;
        }else if (e.getUndeclaredThrowable() instanceof SystemBlockException){
            errorCode = ErrorCode.SENTINEL_SYSTEM;
        }else if (e.getUndeclaredThrowable() instanceof AuthorityException){
            errorCode = ErrorCode.SENTINEL_AUTHORITY;
        }else {
            errorCode = ErrorCode.SENTINEL_FLOW;
        }
        return ResponseMessageBuilder.build(errorCode.getCode(), errorCode.getMesaage());
    }

    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        logger.error("服务器抛出了异常：{}", e);
        return ResponseMessageBuilder.build(ErrorCode.SERVER_EXCEPTION.getCode(), e.getMessage());
    }


}