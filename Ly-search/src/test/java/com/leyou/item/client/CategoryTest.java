package com.leyou.item.client;

import com.leyou.LySearchApp;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.CategoryDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @Author heima
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApp.class)
public class CategoryTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    public  void queryCategoryByIds(){
        //Arrays.asList
        List<CategoryDTO> categoryDTOS = itemClient.findByIds(Arrays.asList(1L, 2L, 3L));
        for (CategoryDTO categoryDTO : categoryDTOS) {
            System.out.println("category 元素:" + categoryDTO);
        }
    }
}
