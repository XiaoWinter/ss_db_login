package com.xiaoadong.ss_use_db.web_security.ouhe;

import com.xiaoadong.ss_use_db.mapper.UserMapper;
import com.xiaoadong.ss_use_db.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Slf4j
public class DbUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("用户登陆验证：{}", username);
        User user = userMapper.findUserByUsername(username);
        if (user == null)
            return null;
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("admin");
        return new DbUserDetails(user.getId(), user.getUsername(), user.getPassword(), roles);
    }
}
