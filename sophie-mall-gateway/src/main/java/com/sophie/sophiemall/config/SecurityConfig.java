package com.sophie.sophiemall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        // 配置跨域、csrf 等
        return http.cors((cors)->{
            CorsConfigurationSource source = new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedMethod("*");
                    config.addAllowedOriginPattern("*");
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true);
                    return config;
                }
            };
            cors.configurationSource(source);
        }).csrf(ServerHttpSecurity.CsrfSpec::disable).build();
    }
}
