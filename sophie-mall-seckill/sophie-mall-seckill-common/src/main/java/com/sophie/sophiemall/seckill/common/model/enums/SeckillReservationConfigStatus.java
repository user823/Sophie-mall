package com.sophie.sophiemall.seckill.common.model.enums;

public enum SeckillReservationConfigStatus {

    PUBLISHED(0),
    ONLINE(1),
    OFFLINE(-1);

    private final Integer code;

    SeckillReservationConfigStatus(Integer code) {
        this.code = code;
    }

    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }

    public static boolean isPublished(Integer status) {
        return PUBLISHED.getCode().equals(status);
    }

    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}