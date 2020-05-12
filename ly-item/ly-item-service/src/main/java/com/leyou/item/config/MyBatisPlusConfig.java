package com.leyou.item.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: TianTian
 * @Date: 2020/5/6 9:23
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * @Description : mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
