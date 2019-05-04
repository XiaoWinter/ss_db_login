package com.xiaoadong.ss_use_db.web_security.security_common;

import com.xiaoadong.ss_use_db.vo.SimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class WebSecurityController {

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Autowired
    private WebSecurityProperties webSecurityProperties;

    /**
     * 当需要身份认证时，跳转到这里
     * 如果是login请求就返回登陆页
     * 其他的就发送提醒返回状态吗401 HttpStatus.UNAUTHORIZED
     * @param request
     * @param response
     */
    @RequestMapping("authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request,response);
        if (savedRequest!=null){
            String target = savedRequest.getRedirectUrl();
            log.info("引发跳转的请求是{}",target);
            String path = StringUtils.substringAfterLast(target, "/");
            if (StringUtils.equalsIgnoreCase("login",path)
                    || StringUtils.equalsIgnoreCase("login.html",path)){
                redirectStrategy.sendRedirect(request,response,webSecurityProperties.getLoginPage());
            }
        }

        return new SimpleResponse("访问的服务需要身份认证,请引导用户到登陆页");


    }
}
