package com.leyou.item.controller;

import com.leyou.common.vo.ExceptionResult;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.service.TbCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: TianTianc
 * @Date: 2020/4/21 19:11
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private TbCategoryService tbCategoryService;

    //http://api.leyou.com/api/item/category/of/parent?pid=0
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> findCategoryByPid(@RequestParam(name="pid") Long pid){
        //查询结果
        List<CategoryDTO> result=tbCategoryService.tbCategoryService(pid);
        return ResponseEntity.ok(result);
    }
}
