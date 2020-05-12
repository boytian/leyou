package com.leyou.test;


import com.leyou.auth.entity.Payload;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import lombok.SneakyThrows;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @version 1.0
 * @Author heima
 **/

public class RsaTest {
    //私钥文件生成的地址
    private String privateKeyPath="C:\\Users\\Administrator\\Desktop\\leyou笔记\\day16\\id_sra";
    //公钥文件的生成地址
    private String publicKeyPath="C:\\Users\\Administrator\\Desktop\\leyou笔记\\day16\\id_sra.pub";

    @SneakyThrows
    @Test
    public void  testRSA(){
        //通过一段 口令  生成两个文件,分别是私钥文件和公钥文件


            RsaUtils.generateKey(publicKeyPath,privateKeyPath,"sdagtdatadgg",1024);


        //获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        System.out.println("私钥对象=" + privateKey);

        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        System.out.println("公钥对象=" + publicKey);


    }

    @Test
    public void testJwt() throws Exception {
        //私钥 , 数字签名
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        //私钥,生成token(jwt)
        String token = JwtUtils.generateTokenExpireInMinutes(new UserInfo(2L, "张三", "admin"),
                privateKey, 5);
        System.out.println("token形式:" + token);


        //微服务有公钥,
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        Payload<UserInfo> user = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);

        //利用公钥解析出token(JWT)
        System.out.println("ID:" + user.getId());

        System.out.println("过期时间:" + user.getExpiration());
        System.out.println("用户信息:" + user.getUserInfo());

        //进一步获取到用户信息
    }
}
