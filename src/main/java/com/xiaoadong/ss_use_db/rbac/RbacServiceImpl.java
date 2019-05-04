package com.xiaoadong.ss_use_db.rbac;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Component("rbacService")
public class RbacServiceImpl implements RbacService{

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails){
            String username = ((UserDetails) principal).getUsername();
            //查询角色拥有的角色，和对应权限，获取所有url
            Set<String> urls = new HashSet<>();//从数据库李查到
            for (String url : urls) {
                if (antPathMatcher.match(url, String.valueOf(request.getRequestURL()))){
                    return true;
                }
            }
        }
        return false;
    }
}
