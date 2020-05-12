package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class PageController {


    @Resource
    private PageService pageService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        // 查询模型数据
        Map<String,Object> itemData = pageService.loadItemData(id);
        // 存入模型数据，因为数据较多，直接存入一个map
        model.addAllAttributes(itemData);
        return "item";
    }
}