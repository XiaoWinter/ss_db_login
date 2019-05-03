package com.xiaoadong.ss_use_db.mapper;

import com.xiaoadong.ss_use_db.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User findUserById(Long id){
        return new User(id,"user"+id,"123456");
    }

    public User findUserByUsername(String username){
        if (StringUtils.startsWith(username,"user")){
            String id = StringUtils.substringAfter(username, "user");
            Long uid;
            try {
                uid = new Long(id);
            }catch (NumberFormatException e){
                return null;
            }
            return new User(uid,username,"123456");
        }
        return null;
    }
}
