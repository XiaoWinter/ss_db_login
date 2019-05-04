package com.xiaoadong.ss_use_db.web_security.authorize;

import com.xiaoadong.ss_use_db.web_security.properties.SecurityConstants;
import com.xiaoadong.ss_use_db.web_security.properties.WebSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 配置允许的url
 */
@Component
@Order(Integer.MIN_VALUE)
public class DbAuthorizeConfigProvider implements AuthorizeConfigProvider {

    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config.antMatchers(
                SecurityConstants.authUrl,//处理认证的url
                webSecurityProperties.getLoginPage(),//登陆的html页面
                SecurityConstants.coderegex/*获取验证码图片的url*/).permitAll();
    }
}
