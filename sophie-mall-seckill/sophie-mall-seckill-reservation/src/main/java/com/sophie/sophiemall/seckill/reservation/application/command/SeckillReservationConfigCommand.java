package com.sophie.sophiemall.seckill.reservation.application.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SeckillReservationConfigCommand implements Serializable {
    private static final long serialVersionUID = 6994147588632776413L;
    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;
    //预约人数上限
    private Integer reserveMaxUserCount;
    //预约开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date reserveStartTime;
    //预约结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date reserveEndTime;

    public boolean isEmpty() {
        return this.goodsId == null
                || this.reserveMaxUserCount == null
                || this.reserveMaxUserCount <= 0
                || this.reserveStartTime == null
                || this.reserveEndTime == null
                || this.reserveStartTime.after(this.reserveEndTime);
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getReserveMaxUserCount() {
        return reserveMaxUserCount;
    }

    public void setReserveMaxUserCount(Integer reserveMaxUserCount) {
        this.reserveMaxUserCount = reserveMaxUserCount;
    }

    public Date getReserveStartTime() {
        return reserveStartTime;
    }

    public void setReserveStartTime(Date reserveStartTime) {
        this.reserveStartTime = reserveStartTime;
    }

    public Date getReserveEndTime() {
        return reserveEndTime;
    }

    public void setReserveEndTime(Date reserveEndTime) {
        this.reserveEndTime = reserveEndTime;
    }
}
