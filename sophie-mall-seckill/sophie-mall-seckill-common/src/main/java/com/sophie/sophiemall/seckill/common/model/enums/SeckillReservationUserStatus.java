package com.sophie.sophiemall.seckill.common.model.enums;

public enum SeckillReservationUserStatus {

    NORMAL(1),
    DELETE(0);


    private final Integer code;

    public static boolean isNormal(Integer status) {
        return NORMAL.getCode().equals(status);
    }

    public static boolean isDeleted(Integer status) {
        return DELETE.getCode().equals(status);
    }

    SeckillReservationUserStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
