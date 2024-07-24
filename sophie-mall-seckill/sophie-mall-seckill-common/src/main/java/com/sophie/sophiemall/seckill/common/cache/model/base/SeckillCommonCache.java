package com.sophie.sophiemall.seckill.common.cache.model.base;

import java.io.Serializable;

public class SeckillCommonCache implements Serializable {
    private static final long serialVersionUID = 2448735813082442223L;
    //缓存数据是否存在
    protected boolean exist;
    //缓存版本号
    protected Long version;
    //稍后再试
    protected boolean retryLater;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isRetryLater() {
        return retryLater;
    }

    public void setRetryLater(boolean retryLater) {
        this.retryLater = retryLater;
    }
}
