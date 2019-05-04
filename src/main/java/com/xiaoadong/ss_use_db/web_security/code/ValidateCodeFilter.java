package com.xiaoadong.ss_use_db.web_security.code;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class ValidateCodeFilter extends OncePerRequestFilter {
    @Setter
    @Getter
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ImageProperty imageProperty;

    /**
     * 处理 请求
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //处理表单登陆请求
        if (StringUtils.equals("/auth/form",StringUtils.substringAfter(request.getRequestURI(),request.getContextPath()))
                && StringUtils.equalsIgnoreCase(request.getMethod(),"post")){
            try{
                validate(request);
            }catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                return;
            }

        }
            filterChain.doFilter(request,response);
    }

    /**
     * 从session中取出验证码来验证传入的验证码
     * @param request
     * @throws ValidateCodeException
     */
    private void validate(HttpServletRequest request) throws ValidateCodeException {
        HttpSession session = request.getSession();
        //取出图片
        ImageCode image = (ImageCode) session.getAttribute(ValidateCodeController.SESSION_KEY);
        //取出表单的验证码
        String vcode = request.getParameter(imageProperty.getInputImgName());

        if (vcode==null)
            throw new ValidateCodeException("验证码不存在");

        if (StringUtils.isBlank(vcode))
            throw new ValidateCodeException("验证码不能为空");

        if (image.isExpired()){
            session.removeAttribute(ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }

        if (!StringUtils.equalsIgnoreCase(vcode,image.getCode()))
            throw new ValidateCodeException("验证码不匹配");

        session.removeAttribute(ValidateCodeController.SESSION_KEY);
    }
}
