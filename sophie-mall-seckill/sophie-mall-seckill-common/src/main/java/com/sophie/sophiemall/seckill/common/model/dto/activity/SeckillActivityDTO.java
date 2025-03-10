package com.sophie.sophiemall.seckill.common.model.dto.activity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class SeckillActivityDTO implements Serializable{
    private static final long serialVersionUID = 1507710823959609002L;
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
    //数据版本
    private Long version;

    public boolean isEmpty(){
        return this.id == null
                && StrUtil.isEmpty(activityName)
                && startTime == null
                && endTime == null
                && status == null;
    }

    public boolean isFallback(){
        return id == -1001L;
    }

    public SeckillActivityDTO() {
    }

    public SeckillActivityDTO(Long id, String activityName, Date startTime, Date endTime, Integer status, String activityDesc, Long version) {
        this.id = id;
        this.activityName = activityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.activityDesc = activityDesc;
        this.version = version;
    }

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
