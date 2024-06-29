package com.sophie.sophiemall.auth.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.sophie.sophiemall.auth.domain.Oauth2TokenDto;
import com.sophie.sophiemall.auth.service.impl.UserServiceImpl;
import com.sophie.sophiemall.common.api.CommonResult;
import com.sophie.sophiemall.common.constant.AuthConstant;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;

/**
 * 自定义oauth2获取令牌接口
 */

@RestController
@Tag(name = "AuthController", description = "认证中心登录认证")
@RequestMapping("/oauth")
public class AuthController {
    @Autowired
    UserServiceImpl userService;

    @Operation(summary = "Oauth2获取token")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public CommonResult<Oauth2TokenDto> postAccessToken() throws HttpRequestMethodNotSupportedException {
        SaRequest req = SaHolder.getRequest();
        SaResponse resp = SaHolder.getResponse();
        SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
        SaResult res = (SaResult) SaOAuth2Handle.password(req, resp, cfg);

        // 登录成功
        Map<String, Object> data = (Map<String, Object>)res.getData();
        Oauth2TokenDto dto = Oauth2TokenDto.builder()
                .token((String) data.get("access_token"))
                .refreshToken((String) data.get("refresh_token"))
                .tokenHead(AuthConstant.JWT_TOKEN_PREFIX)
                .expiresIn(((Long)data.get("expires_in")).intValue()).build();
        return CommonResult.success(dto);
    }

    @Autowired
    public void setSaOAuth2Config(SaOAuth2Config cfg) {
        cfg.setDoLoginHandle(userService::loginHandler);
    }
}
