package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: TianTian
 * @Date: 2020/4/25 16:02
 */
@Configuration
public class OSSConfig {
    @Bean
    public OSS ossClient(OSSProperties prop) {
        return new OSSClientBuilder()
                .build(prop.getEndpoint(), prop.getAccessKeyId(), prop.getAccessKeySecret());
    }
}
