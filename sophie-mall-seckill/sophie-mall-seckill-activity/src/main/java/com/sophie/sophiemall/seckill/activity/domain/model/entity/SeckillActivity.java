package com.sophie.sophiemall.seckill.activity.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophie.sophiemall.seckill.common.model.enums.SeckillActivityStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
public class SeckillActivity implements Serializable {
    private static final long serialVersionUID = -7079319520596736847L;
    //活动id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    //活动名称
    private String activityName;
    //活动开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date startTime;
    //活动结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date endTime;
    //活动状态 0：已发布； 1：上线； -1：下线
    private Integer status;
    //活动描述
    private String activityDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public boolean validateParams(){
        if (StringUtils.isEmpty(activityDesc)
                || startTime == null
                || endTime == null
                || endTime.before(startTime)
                || endTime.before(new Date())){
            return false;
        }
        return true;
    }

    public boolean isOnline(){
        return SeckillActivityStatus.isOnline(status);
    }

    public boolean isInSeckilling(){
        Date currentDate = new Date();
        return startTime.before(currentDate) && endTime.after(currentDate);
    }
}
