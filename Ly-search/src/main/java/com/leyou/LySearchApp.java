package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @version 1.0
 * @Author heima
 **/
@SpringBootApplication
@EnableDiscoveryClient
//如果需要调用itemservice的接口, feign
@EnableFeignClients
public class LySearchApp {
    public static void main(String[] args) {
        SpringApplication.run(LySearchApp.class, args);
    }
}
