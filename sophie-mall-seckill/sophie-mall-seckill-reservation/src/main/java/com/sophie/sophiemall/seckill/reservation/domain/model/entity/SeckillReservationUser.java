package com.sophie.sophiemall.seckill.reservation.domain.model.entity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SeckillReservationUser implements Serializable {
    private static final long serialVersionUID = 3249880048600298460L;
    //预约记录id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    //预约配置id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long reserveConfigId;
    //商品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long goodsId;
    //商品名称
    private String goodsName;
    //用户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    //预约时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date reserveTime;
    //状态,1:正常;0:删除
    private Integer status;

    public boolean isEmpty(){
        return id == null
                || reserveConfigId == null
                || goodsId == null
                || StrUtil.isEmpty(goodsName)
                || userId == null
                || reserveTime == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReserveConfigId() {
        return reserveConfigId;
    }

    public void setReserveConfigId(Long reserveConfigId) {
        this.reserveConfigId = reserveConfigId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(Date reserveTime) {
        this.reserveTime = reserveTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
