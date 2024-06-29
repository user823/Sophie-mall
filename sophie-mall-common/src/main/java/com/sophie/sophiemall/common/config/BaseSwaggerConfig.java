package com.sophie.sophiemall.common.config;

import com.sophie.sophiemall.common.domain.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

/**
 * Swagger基础配置
 */
public abstract class BaseSwaggerConfig {
    // 自定义swagger配置
    public abstract SwaggerProperties swaggerProperties();

    @Bean
    public OpenAPI springOpenAPI() {
        SwaggerProperties swaggerProperties = swaggerProperties();
        return new OpenAPI().info(info(swaggerProperties));
    }

    private Info info(SwaggerProperties swaggerProperties) {
        Info res = new Info();
        Contact c = new Contact();
        c.setEmail(swaggerProperties.getContactEmail());
        c.setUrl(swaggerProperties.getContactUrl());
        c.setName(swaggerProperties.getContactName());
        res.setTitle(swaggerProperties.getTitle());
        res.setDescription(swaggerProperties.getDescription());
        res.contact(c);
        res.setVersion(swaggerProperties.getVersion());
        return res;
    }
}
