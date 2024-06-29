package com.sophie.sophiemall.auth.config;

import com.sophie.sophiemall.common.config.BaseSwaggerConfig;
import com.sophie.sophiemall.common.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger相关配置
 */
@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.macro.mall.auth.controller")
                .title("mall认证中心")
                .description("mall认证中心相关接口文档")
                .contactName("macro")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
