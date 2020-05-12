package com.leyou.user.controller;

import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.service.TbUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: TianTian
 * @Date: 2020/5/10 15:33
 */
@RestController
public class UserController {


    @Resource
    private TbUserService userService;


    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable("type") Long type) {

        return ResponseEntity.ok(userService.checkUserData(data,type));
    }

    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        //调用发短信的接口
        userService.sendCode(phone);
        //返回nocontent
        return  ResponseEntity.noContent().build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid TbUser user, @RequestParam("code") String code){
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUser(@RequestParam(name = "username")String username,
                                             @RequestParam(name = "password")String password){
        return ResponseEntity.ok(userService.queryUser(username,password));
    }
}
