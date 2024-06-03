package com.sophie.sophiemall.auth.config;

import com.sophie.sophiemall.auth.constant.KeyStoreConstant;
import com.sophie.sophiemall.auth.component.JwtTokenEnhancer;
import com.sophie.sophiemall.auth.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * 授权服务器配置
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenEnhancer jwtTokenEnhancer;

    // 配置oauth2的客户端详情信息，分别配置admin-app 和 main-app两个客户端
    // 客户端模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("admin-app")    // 配置客户端id
                .secret(passwordEncoder.encode(KeyStoreConstant.KEYSTORE_PASSWORD))   // 授权服务器的密码
                .scopes("all")  // 授权范围
                .authorizedGrantTypes("password", "refresh_token", "client_credentials")  // 授权类型（客户端模式）
                .accessTokenValiditySeconds(3600*24)    // 访问令牌有效期
                .refreshTokenValiditySeconds(3600*24*7) // 刷新令牌的有效期
                .and()
                .withClient("main-app")
                .secret(passwordEncoder.encode(KeyStoreConstant.KEYSTORE_PASSWORD))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token", "client_credentials")
                .accessTokenValiditySeconds(3600*24)
                .refreshTokenValiditySeconds(3600*24*7);
    }

    // 配置oauth2 授权服务器的endpoint
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
        endpoints.authenticationManager(authenticationManager) // 密码模式需要配置AuthenticationManager
                .userDetailsService(userDetailsService) //配置加载用户信息的服务，用于刷新令牌
                .accessTokenConverter(accessTokenConverter()) // 配置令牌转换器
                .tokenEnhancer(enhancerChain);  // 配置令牌增强器链（添加额外信息、令牌转换器等）
    }

    //开启允许客户端表单验证
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }

    // oauth2认证框架下 认证信息和jwt信息之间进行转换
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), KeyStoreConstant.KEYSTORE_PASSWORD.toCharArray());
        return keyStoreKeyFactory.getKeyPair(KeyStoreConstant.KEY_ALIAS, KeyStoreConstant.KEYSTORE_PASSWORD.toCharArray());
    }

}