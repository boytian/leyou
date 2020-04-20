package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 13:03
 */
@SpringBootApplication
@EnableEurekaServer
public class LyRegistry {
    public static void main(String [] args) {
        SpringApplication.run(LyRegistry.class,args);
    }

}
