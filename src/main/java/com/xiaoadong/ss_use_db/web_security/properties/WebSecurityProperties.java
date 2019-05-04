package com.xiaoadong.ss_use_db.web_security.properties;

import com.xiaoadong.ss_use_db.web_security.security_common.LoginType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "securityweb.dbauth")
@Component(value = "WebSecurityProperties")
@Getter
@Setter
public class WebSecurityProperties {
    //认证后的行为，返回json或者页面跳转
    private LoginType loginType = LoginType.JSON;

    //表单登陆html页,放在static里的HTML
    private  String loginPage = "/login.html";

    //认证成功后访问的url
    private  String successUrl = "/";

    //表单提交到的url
    private  String loginProcessingUrl = "/auth/form";


}
