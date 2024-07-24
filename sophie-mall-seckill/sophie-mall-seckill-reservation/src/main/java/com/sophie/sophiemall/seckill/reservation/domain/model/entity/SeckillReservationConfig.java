package com.sophie.sophiemall.seckill.reservation.domain.model.entity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SeckillReservationConfig implements Serializable {
    private static final long serialVersionUID = -9191555379369224853L;
    //配置id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;
    //商品名称
    private String goodsName;
    //预约人数上限
    private Integer reserveMaxUserCount;
    //当前预约人数
    private Integer reserveCurrentUserCount;

    //预约开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date reserveStartTime;

    //预约结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date reserveEndTime;

    //秒杀开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date seckillStartTime;

    //秒杀结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date seckillEndTime;

    //状态，0：已发布，1：上线；-1：下线
    private Integer status;

    public boolean isEmpty(){
        return this.id == null
                || this.goodsId == null
                || StrUtil.isEmpty(goodsName)
                || this.reserveMaxUserCount == null
                || this.reserveMaxUserCount <= 0
                || this.reserveCurrentUserCount == null
                || this.reserveCurrentUserCount < 0
                || this.reserveMaxUserCount < this.reserveCurrentUserCount
                || this.reserveStartTime == null
                || this.reserveEndTime == null
                || this.reserveStartTime.after(this.reserveEndTime)
                || this.seckillStartTime == null
                || this.seckillEndTime == null
                || this.seckillStartTime.after(this.seckillEndTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getReserveMaxUserCount() {
        return reserveMaxUserCount;
    }

    public void setReserveMaxUserCount(Integer reserveMaxUserCount) {
        this.reserveMaxUserCount = reserveMaxUserCount;
    }

    public Integer getReserveCurrentUserCount() {
        return reserveCurrentUserCount;
    }

    public void setReserveCurrentUserCount(Integer reserveCurrentUserCount) {
        this.reserveCurrentUserCount = reserveCurrentUserCount;
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

    public Date getSeckillStartTime() {
        return seckillStartTime;
    }

    public void setSeckillStartTime(Date seckillStartTime) {
        this.seckillStartTime = seckillStartTime;
    }

    public Date getSeckillEndTime() {
        return seckillEndTime;
    }

    public void setSeckillEndTime(Date seckillEndTime) {
        this.seckillEndTime = seckillEndTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
