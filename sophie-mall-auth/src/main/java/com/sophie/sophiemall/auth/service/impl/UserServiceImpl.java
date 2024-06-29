package com.sophie.sophiemall.auth.service.impl;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.sophie.sophiemall.auth.constant.MessageConstant;
import com.sophie.sophiemall.auth.domain.SecurityUser;
import com.sophie.sophiemall.auth.service.UmsAdminService;
import com.sophie.sophiemall.auth.service.UmsMemberService;
import com.sophie.sophiemall.common.constant.AuthConstant;
import com.sophie.sophiemall.common.domain.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.crypto.digest.BCrypt;

import java.security.Security;

/**
 * 用户管理业务类
 */
@Service
public class UserServiceImpl {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private HttpServletRequest request;

    public SaResult loginHandler(String username, String password) throws SaTokenException {
        String clientId = request.getParameter("client_id");
        UserDto userDto;

        if(AuthConstant.ADMIN_CLIENT_ID.equals(clientId)){
            userDto = adminService.loadUserByUsername(username);
        }else{
            userDto = memberService.loadUserByUsername(username);
        }

        if (userDto==null) {
            throw new SaTokenException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        userDto.setClientId(clientId);

        SecurityUser securityUser = new SecurityUser(userDto);
        if (!securityUser.isEnabled()) {
            throw new SaTokenException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new SaTokenException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new SaTokenException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new SaTokenException(MessageConstant.CREDENTIALS_EXPIRED);
        } else if (!(username.equals(securityUser.getUsername()) && BCrypt.checkpw(password, securityUser.getPassword()))) {
            throw new SaTokenException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }

        StpUtil.login(securityUser.getUsername());
        StpUtil.getSessionByLoginId(securityUser.getUsername()).set(SaSession.USER, userDto);
        return SaResult.ok();
    }

}
