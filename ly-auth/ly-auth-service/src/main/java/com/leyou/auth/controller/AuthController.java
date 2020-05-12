package com.leyou.auth.controller;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: TianTian
 * @Date: 2020/5/11 9:22
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> accredit(HttpServletResponse response,
                                         @RequestParam("username") String username, @RequestParam("password") String password){
        authService.login(username,password,response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(HttpServletResponse response, HttpServletRequest request){
        return ResponseEntity.ok(authService.verify(response,request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        authService.logout(request,response);
        return ResponseEntity.noContent().build();
    }

}
