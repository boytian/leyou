package com.leyou.page.test;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCreatePage {

    @Autowired
    private PageService pageService;
    @Test
    public void testCreateHtml(){
        List<Long> spuIdList = Arrays.asList(160l,170l);
        for (Long spuId : spuIdList) {
            pageService.createItemHtml(spuId);
        }

    }
}