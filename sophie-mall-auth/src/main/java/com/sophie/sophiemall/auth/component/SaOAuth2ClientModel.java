package com.sophie.sophiemall.auth.component;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import com.sophie.sophiemall.common.constant.AuthConstant;
import org.springframework.stereotype.Component;

@Component
public class SaOAuth2ClientModel extends SaOAuth2Template {
    @Override
    public SaClientModel getClientModel(String clientId) {
        if (AuthConstant.ADMIN_CLIENT_ID.equals(clientId) || AuthConstant.PORTAL_CLIENT_ID.equals(clientId)) {
            return new SaClientModel()
                    .setClientId(clientId)
                    .setClientSecret("123456")
                    .setAllowUrl("*")
                    .setContractScope("all")
                    .setAccessTokenTimeout(24 * 3600)
                    .setRefreshTokenTimeout(24 * 3600 * 7);
        }
        return null;
    }
}
