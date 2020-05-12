package com.leyou.user;

import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: TianTian
 * @Date: 2020/5/11 9:39
 */
@FeignClient("user-service")
public interface UserClient {

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    UserDTO queryUser(@RequestParam("username")String username,
                                           @RequestParam("password")String password);
}
