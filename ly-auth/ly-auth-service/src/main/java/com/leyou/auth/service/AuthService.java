package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.Payload;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.UserClient;
import com.leyou.user.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: TianTian
 * @Date: 2020/5/11 9:23
 */
@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;
    private static final String USER_ROLE = "role_user";

    public void login(String username, String password, HttpServletResponse response) {
        //查询用户
        UserDTO userDTO = userClient.queryUser(username, password);
        //将用户信息写入token
        UserInfo userInfo = new UserInfo(userDTO.getId(), userDTO.getUsername(), USER_ROLE);
        //生成token
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getUser().getExpice());
        //写入token，防止xxs攻击httponly，设置domain，cookies名称
        CookieUtils.newCookieBuilder().response(response)
                .httpOnly(true)
                .domain(jwtProperties.getUser().getCookieDomain())
                .name(jwtProperties.getUser()
                        .getCookieName()).value(token).build();
        try {

        } catch (Exception e) {
            throw new LyException(ExcptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    public UserInfo verify(HttpServletResponse response, HttpServletRequest request) {

        try {
            //读取cookie
            String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
            //解密token（jwt）
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
            Boolean b = stringRedisTemplate.hasKey(payload.getId());
            if (b!=null&& b) {
                throw  new LyException(ExcptionEnum.UNAUTHORIZED);
            }
            //添加刷新的逻辑
            //获取过期时间
            Date expiration = payload.getExpiration();
            //获取刷新时间
            DateTime refreshTime = new DateTime(expiration.getTime()).minusMillis(jwtProperties.getUser().getMinRefreshInterval());
            if (refreshTime.isBefore(System.currentTimeMillis())) {
                //过了刷新时间，生成新的token
                String newToken = JwtUtils.generateTokenExpireInMinutes(payload.getUserInfo(), jwtProperties.getPrivateKey(), jwtProperties.getUser().getExpice());
                //写入token，防止xxs攻击httponly，设置domain，cookies名称
                CookieUtils.newCookieBuilder().response(response)
                        .httpOnly(true)
                        .domain(jwtProperties.getUser().getCookieDomain())
                        .name(jwtProperties.getUser()
                                .getCookieName()).value(newToken).build();
            }

            return payload.getUserInfo();
        } catch (Exception e) {
        throw new LyException(ExcptionEnum.UNAUTHORIZED);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            //取出token
            //读取cookie
            String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
            //计算有效时间
            String id = payload.getId();
            Date expiration = payload.getExpiration();
            long l = expiration.getTime() - System.currentTimeMillis();
            //存到redis中
            stringRedisTemplate.opsForValue().set(id,"1",l, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //干掉cookie
            CookieUtils.deleteCookie(jwtProperties.getUser().getCookieName(),jwtProperties.getUser().getCookieDomain()
            ,response);
        }
    }
}
