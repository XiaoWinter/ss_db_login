package com.xiaoadong.ss_use_db.web_security.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbAuthorizeConfigManager implements AuthorizeConfigManager{

    @Autowired
    private List<AuthorizeConfigProvider> authorizeConfigProvider;

    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        for (AuthorizeConfigProvider configProvider : authorizeConfigProvider) {
            configProvider.config(config);
        }
//        所有配置完后，剩下的需要权限
        config.anyRequest().authenticated();
    }
}
