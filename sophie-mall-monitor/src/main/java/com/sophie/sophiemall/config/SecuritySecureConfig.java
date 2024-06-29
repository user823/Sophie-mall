package com.sophie.sophiemall.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecuritySecureConfig {
    private final String adminContextPath;
    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(adminContextPath + "/assets/**").permitAll();
                    auth.requestMatchers(adminContextPath + "/login").permitAll();
                    auth.anyRequest().authenticated();
                })
                //2.配置登录和登出路径
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and().logout().logoutUrl(adminContextPath + "/logout").and()
                //3.开启http basic 支持，admin-client 注册是需要使用
                .httpBasic().and()
                .csrf()
                //4.开启基于cookie的csrf保护
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //5.忽略这些路径的csrf保护以便admin-client注册
                .ignoringRequestMatchers(adminContextPath + "/instances", adminContextPath + "/actuator/**" );

        return http.build();
    }
}
