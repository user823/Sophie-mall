package com.sophie.sophiemall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 用户登录参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsAdminLoginParam {
    @NotNull
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @NotNull
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
