package com.xiaoadong.ss_use_db.web_security.ouhe;

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


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .authorizeRequests()
                .regexMatchers("/authentication/require",webSecurityProperties.getLoginPage()).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl("/auth/form")
                .defaultSuccessUrl("/")
                .successHandler(dbAuthenticationSuccessHandler())
                .failureHandler(dbAuthenticationFailureHandler())
                .and()
                .csrf().disable()
        ;

    }
}
