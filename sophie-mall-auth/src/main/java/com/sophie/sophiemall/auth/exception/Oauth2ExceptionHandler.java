package com.sophie.sophiemall.auth.exception;

import cn.dev33.satoken.exception.SaTokenException;
import com.sophie.sophiemall.common.api.CommonResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * oauth2 全局异常处理
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = SaTokenException.class)
    public CommonResult handleOauth2(SaTokenException e) {
        return CommonResult.failed(e.getMessage());
    }
}
