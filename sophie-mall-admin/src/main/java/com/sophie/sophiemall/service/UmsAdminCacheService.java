package com.sophie.sophiemall.service;

import com.sophie.sophiemall.model.UmsAdmin;
import com.sophie.sophiemall.model.UmsResource;

import java.util.List;

/**
 * 后台用户缓存操作类
 */
public interface UmsAdminCacheService {
    /**
     * 删除后台用户缓存
     */
    void delAdmin(String username);

    /**
     * 获取缓存后台用户信息
     */
    UmsAdmin getAdmin(String username);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UmsAdmin admin);
}
