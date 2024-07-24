package com.sophie.sophiemall.seckill.order.application.model.result;

import com.sophie.sophiemall.seckill.common.exception.ErrorCode;

public class OrderTaskSubmitResult {

    private boolean success;
    private Integer code;
    private String message;

    public static OrderTaskSubmitResult ok() {
        return new OrderTaskSubmitResult()
                .setSuccess(true);
    }

    public static OrderTaskSubmitResult failed(ErrorCode errorCode) {
        return new OrderTaskSubmitResult()
                .setSuccess(false)
                .setCode(errorCode.getCode())
                .setMessage(errorCode.getMesaage());
    }

    public OrderTaskSubmitResult() {
    }

    public OrderTaskSubmitResult(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public OrderTaskSubmitResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public OrderTaskSubmitResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public OrderTaskSubmitResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
