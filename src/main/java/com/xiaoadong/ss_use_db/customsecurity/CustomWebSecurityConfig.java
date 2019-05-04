package com.xiaoadong.ss_use_db.customsecurity;

import com.xiaoadong.ss_use_db.ouhe.DbUserDetailService;
import com.xiaoadong.ss_use_db.web_security.authorize.AuthorizeConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomWebSecurityConfig implements AuthorizeConfigProvider{

    @Bean("dbUserDetailService")
    public UserDetailsService dbUserDetailService(){
        return new DbUserDetailService();
    }

    @Override
    public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        try {
            config
                    .antMatchers("/admin")
                    .hasRole("ADMIN")
                    .anyRequest()
                    //没看见效果
                    .access("@rbacService.hasPermission(request, authentication)")
                    .and()
                    .csrf()
                    .disable();
        } catch (Exception e) {
            log.debug("自定义权限配置失败");
            throw new RuntimeException("自定义权限配置失败");
        }
    }
}
