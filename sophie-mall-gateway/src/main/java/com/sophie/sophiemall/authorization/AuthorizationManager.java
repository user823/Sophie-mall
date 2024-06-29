package com.sophie.sophiemall.authorization;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.sophie.sophiemall.common.constant.AuthConstant;
import com.sophie.sophiemall.common.domain.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 鉴权管理器，用于判断是否具有资源管理权限
 */
@Component
public class AuthorizationManager implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return getRoleList(loginId, loginType);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserDto userDto = StpUtil.getSessionByLoginId(loginId).getModel(SaSession.USER, UserDto.class);
        return userDto.getRoles();
    }

}