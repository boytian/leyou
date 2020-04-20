package com.leyou.test;

import com.leyou.common.utils.IdWorker;
import org.junit.Test;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 13:51
 */
public class TestCommon {

    @Test
    public void test1(){
        IdWorker idWorker = new IdWorker(1,1);
        long id = idWorker.nextId();
        System.out.println("分布式的id为："+id);
    }
}
