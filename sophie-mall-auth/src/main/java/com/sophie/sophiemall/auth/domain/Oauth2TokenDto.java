package com.sophie.sophiemall.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Oauth2获取Token返回信息封装
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class Oauth2TokenDto {
    @Schema(name = "访问令牌")
    private String token;
    @Schema(name = "刷令牌")
    private String refreshToken;
    @Schema(name = "访问令牌头前缀")
    private String tokenHead;
    @Schema(name = "有效时间（秒）")
    private int expiresIn;
}