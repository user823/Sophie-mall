package com.sophie.sophiemall.seckill.common.response;

import java.io.Serializable;

public class ResponseMessage<T> implements Serializable {

    private Integer code;
    private T data;


    public ResponseMessage() {
    }

    public ResponseMessage(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseMessage(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
