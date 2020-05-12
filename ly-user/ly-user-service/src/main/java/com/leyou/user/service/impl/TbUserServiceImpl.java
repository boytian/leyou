package com.leyou.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {
    //redis中的key前缀
    private static final String  KEY_PREFIX = "ly:user:verify:phone:";

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Boolean checkUserData(String data, Long type) {
        //构造一个查询
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper();

        if (type==1){
            //查询名字，type==1
            //queryWrapper.lambda().eq(TbUser::getUsername,data);
            queryWrapper.eq("username",data);
        }else if(type==2){
            //查询手机号，type==0
            //queryWrapper.lambda().eq(TbUser::getPhone,data);
            queryWrapper.eq("phone",data);
        }else {
            throw new LyException(ExcptionEnum.PRICE_CANNOT_BE_NULL);
        }
        return this.count(queryWrapper)==0;
    }

    @Override
    public void sendCode(String phone) {
        //phone格式是否正确
        if (!RegexUtils.isPhone(phone)){
            throw  new LyException(ExcptionEnum.INVALID_PHONE_NUMBER);
        }
        //添加redis验证码
        String code = RandomStringUtils.randomNumeric(7);
        stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,3, TimeUnit.MINUTES);
        //给ly-msm发消息，发送验证码
        Map<String,String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME+":"+VERIFY_CODE_TAGS,map);
    }


    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @Override
    public void register(TbUser user, String code) {
//        验证用户信息
//        验证短信验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(cacheCode,code)){
            throw new LyException(ExcptionEnum.INVALID_VERIFY_CODE);
        }
//        用户密码加密,spring提供的BcryptPasswordEncode
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
//        保存
        boolean b = this.save(user);
        if(!b){
            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Override
    public UserDTO queryUser(String username, String password) {
        //1.查询是否存在用户
        //1.1创建构造器
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        //1.2根据用户名查数据
        queryWrapper.eq("username",username);
        TbUser user = this.getOne(queryWrapper);

        //如果查询的结果为空,说明用户信息错误
        if (user==null) {
            throw new LyException(ExcptionEnum.INVALID_USERNAME_PASSWORD);
        }

        //如果用户存在,进一步验证密码是否正确,passwordEncoder.做解密,匹配match方法
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        //如果匹配不成功,报密码错误
        if (!matches) {
            throw new LyException(ExcptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //如果都没有问题,TbUser-->UserDTO,返回userDTO
        UserDTO userDTO = BeanHelper.copyProperties(user, UserDTO.class);
        return userDTO;
    }
}
