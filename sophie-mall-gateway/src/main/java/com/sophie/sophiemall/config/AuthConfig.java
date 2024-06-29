package com.sophie.sophiemall.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.sophie.sophiemall.common.api.CommonResult;
import com.sophie.sophiemall.common.constant.AuthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class AuthConfig {
    @Autowired
    IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(ignoreUrls.toArray(new String[0])) // 白名单路径直接放行
                .setAuth(obj -> {
                    // 授权函数，未授权时直接抛出异常
                    ServerHttpRequest req = SaReactorSyncHolder.getContext().getRequest();
                    URI uri = req.getURI();
                    // 预检函数，直接放行
                    if (req.getMethod().equals(HttpMethod.OPTIONS)) {
                        return ;
                    }
                    // 检查登录
                    String realToken = StpUtil.getTokenValue();
                    AccessTokenModel m = SaOAuth2Util.checkAccessToken(realToken);

                    //不同用户体系登录不允许互相访问
                    if (AuthConstant.ADMIN_CLIENT_ID.equals(m.clientId) && !SaRouter.isMatch(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
                        throw new NotPermissionException("管理员不能访问非管理员路径");
                    }
                    if (AuthConstant.PORTAL_CLIENT_ID.equals(m.clientId) && SaRouter.isMatch(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
                        throw new NotPermissionException("非管理员不能访问管理员路径");
                    }
                    // 非管理员路径直接放行
                    if (!SaRouter.isMatch(AuthConstant.ADMIN_URL_PATTERN, uri.getPath())) {
                        return ;
                    }
                    // 管理端路径需校验权限
                    Map<Object, Object> resourceRolesMap = redisTemplate.opsForHash().entries(AuthConstant.RESOURCE_ROLES_MAP_KEY);
                    Iterator<Object> iterator = resourceRolesMap.keySet().iterator();
                    List<String> authorities = new ArrayList<>();
                    while (iterator.hasNext()) {
                        String pattern = (String) iterator.next();
                        if (SaRouter.isMatch(pattern, uri.getPath())) {
                            authorities.addAll(Convert.toList(String.class, resourceRolesMap.get(pattern)));
                        }
                    }
                    authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());
//                    if (authorities.isEmpty()) {
//                        return ;
//                    }
//                    List<String> perms = SaManager.getStpInterface().getPermissionList(m.loginId, m.clientId);
//                    for (String perm : perms) {
//                        System.out.println(perm);
//                        if (authorities.contains(perm)) {
//                            return ;
//                        }
//                    }
//                    throw new NotPermissionException("当前用户没有权限访问该路径");
                });
    }
}