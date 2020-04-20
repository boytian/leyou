package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 13:10
 */
@SpringBootApplication
@EnableZuulProxy
public class LyGateway {
    public static void main(String [] args) {
        SpringApplication.run(LyGateway.class,args);
    }

}
