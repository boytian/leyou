package com.leyou.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
public interface TbUserService extends IService<TbUser> {

    Boolean checkUserData(String data, Long type);

    void sendCode(String phone);

    void register(TbUser user, String code);

    UserDTO queryUser(String userName, String passWord);
}
