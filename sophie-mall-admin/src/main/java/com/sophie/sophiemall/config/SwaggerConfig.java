package com.sophie.sophiemall.config;

import com.sophie.sophiemall.common.config.BaseSwaggerConfig;
import com.sophie.sophiemall.common.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档相关配置
 */
@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.macro.mall.controller")
                .title("sophie-mall-admin")
                .description("mall后台相关接口文档")
                .contactName("macro")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
