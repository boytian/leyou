package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {


    private String pubKeyPath;// 公钥

    private String priKeyPath;// 私钥

    private PublicKey publicKey; // 公钥

    private PrivateKey privateKey; // 私钥

    private UserTokenProperties user=new UserTokenProperties();

    @Data
    public class UserTokenProperties {
        private int expice;
        private String cookieName;
        private String cookieDomain;
        private int minRefreshInterval;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //直走各个属性诸如完成后加载
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}