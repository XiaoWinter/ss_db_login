package com.xiaoadong.ss_use_db.web_security.properties;

/**
 * 有controller的url
 */
public interface SecurityConstants {
    //需要认证时执行的url,决定时返回登陆页还是别的
    public static String authUrl = "/authentication/require";
    //验证码的url=>"/code/"+{type}路径参数的形式决定是u什么验证码
    public static String codeurl = "/code/";
    //允许任何人访问验证码的请求路径
    public static String coderegex = "/code/*";
}
