package com.xiaoadong.ss_use_db.web_security.security_common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "securityweb.dbauth")
@Component(value = "WebSecurityProperties")
@Getter
@Setter
public class WebSecurityProperties {
    private LoginType loginType = LoginType.JSON;
    private String loginPage = "/login.html";
}
