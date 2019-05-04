package com.xiaoadong.ss_use_db.web_security;


import com.xiaoadong.ss_use_db.web_security.authorize.AuthorizeConfigManager;
import com.xiaoadong.ss_use_db.web_security.code.ValidateCodeFilter;
import com.xiaoadong.ss_use_db.web_security.properties.SecurityConstants;
import com.xiaoadong.ss_use_db.web_security.properties.WebSecurityProperties;
import com.xiaoadong.ss_use_db.web_security.security_common.DbAuthenticationFailureHandler;
import com.xiaoadong.ss_use_db.web_security.security_common.DbAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebSecurityProperties webSecurityProperties;


    @Bean
    public AuthenticationSuccessHandler dbAuthenticationSuccessHandler(){
        return new DbAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler dbAuthenticationFailureHandler(){
        return new DbAuthenticationFailureHandler();
    }

    @Bean
    public Filter ValidateCodeFilter(){
        ValidateCodeFilter filter = new ValidateCodeFilter();
        filter.setAuthenticationFailureHandler(dbAuthenticationFailureHandler());
        return filter;
    }

    @Autowired
    private AuthorizeConfigManager dbAuthorizeConfigManager;


    @Override
    protected void configure(HttpSecurity http) throws Exception {



        http
                .addFilterBefore(ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin()//使用表单登陆
                .loginPage(SecurityConstants.authUrl)//需要认证就转到/authentication/require；它在决定下一步如何处理
                .loginProcessingUrl(webSecurityProperties.getLoginProcessingUrl())//表单提交的url,提交的数据被UsernamePasswordToken接收
                .defaultSuccessUrl(webSecurityProperties.getSuccessUrl())
                .successHandler(dbAuthenticationSuccessHandler())
                .failureHandler(dbAuthenticationFailureHandler())
        ;
        dbAuthorizeConfigManager.config(http.authorizeRequests());
    }
}
