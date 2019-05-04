package com.xiaoadong.ss_use_db.web_security.ouhe;

import com.xiaoadong.ss_use_db.web_security.code.ValidateCodeFilter;
import com.xiaoadong.ss_use_db.web_security.security_common.DbAuthenticationFailureHandler;
import com.xiaoadong.ss_use_db.web_security.security_common.DbAuthenticationSuccessHandler;
import com.xiaoadong.ss_use_db.web_security.security_common.WebSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Bean
    public UserDetailsService dbUserDetailService(){
        return new DbUserDetailService();
    }

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


    @Override
    protected void configure(HttpSecurity http) throws Exception {



        http
                .addFilterBefore(ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()//请求认证
                .regexMatchers(
                        "/authentication/require",//处理认证的url
                        webSecurityProperties.getLoginPage(),//登陆的html页面
                        "/code/image"/*获取验证码图片的url*/).permitAll()//允许
                .anyRequest()//任何
                .authenticated()//需要认证
                .and()
                .formLogin()//使用表单登陆
                .loginPage("/authentication/require")//需要认证就转到/authentication/require；它在决定下一步如何处理
                .loginProcessingUrl("/auth/form")//表单提交的url,提交的数据被UsernamePasswordToken接收
                .defaultSuccessUrl("/")
                .successHandler(dbAuthenticationSuccessHandler())
                .failureHandler(dbAuthenticationFailureHandler())
                .and()
                .csrf().disable()
        ;

    }
}
