package com.sophie.sophiemall.seckill.common.response;

public class ResponseMessageBuilder {

    public static <T> ResponseMessage<T> build(Integer code, T body){
        return new ResponseMessage<T>(code, body);
    }


    public static <T> ResponseMessage<T> build(Integer code){
        return new ResponseMessage<T>(code);
    }


}
