package com.sophie.sophiemall.main.service;

import com.sophie.sophiemall.model.UmsMember;

/**
 * 会员信息缓存业务类
 */
public interface UmsMemberCacheService {
    /**
     * 删除会员用户缓存
     */
    void delMember(String memberName);

    /**
     * 获取会员用户缓存
     */
    UmsMember getMember(String memberName);

    /**
     * 设置会员用户缓存
     */
    void setMember(UmsMember member);

    /**
     * 设置验证码
     */
    void setAuthCode(String telephone, String authCode);

    /**
     * 获取验证码
     */
    String getAuthCode(String telephone);
}
