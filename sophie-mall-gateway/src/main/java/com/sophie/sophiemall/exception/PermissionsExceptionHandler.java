package com.sophie.sophiemall.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.sophie.sophiemall.common.api.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PermissionsExceptionHandler {
    // 处理未登录和未认证异常
    @ExceptionHandler({NotLoginException.class, NotPermissionException.class})
    public CommonResult<String> AccessDeniedHandler(Exception e) {
        e.printStackTrace();
        return CommonResult.success(e.getMessage());
    }
}