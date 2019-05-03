package com.xiaoadong.ss_use_db.web;

import com.xiaoadong.ss_use_db.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("user/{uid}")
    public ResponseEntity<User> findUserById(){
        return ResponseEntity.ok(new User());
    }

    @PostMapping("user/d")
    public ResponseEntity<User> login(User user){
        return ResponseEntity.ok(user);
    }
}
