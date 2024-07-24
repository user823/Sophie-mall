package com.sophie.sophiemall.seckill.common.cache.model;

import com.sophie.sophiemall.seckill.common.cache.model.base.SeckillCommonCache;

public class SeckillBusinessCache<T> extends SeckillCommonCache {

    private T data;

    public SeckillBusinessCache<T> with(T data){
        this.data = data;
        this.exist = true;
        return this;
    }

    public SeckillBusinessCache<T> withVersion(Long version){
        this.version = version;
        return this;
    }

    public SeckillBusinessCache<T> retryLater(){
        this.retryLater = true;
        return this;
    }

    public SeckillBusinessCache<T> notExist(){
        this.exist = false;
        this.version = -1L;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
