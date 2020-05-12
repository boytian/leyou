package com.leyou.item.controller;

import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.service.TbCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * 根据品牌id 查询分类集合
     * @param id
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByBrandId(@RequestParam(name = "id")Long id){
        return ResponseEntity.ok(tbCategoryService.findCategoryListByBrandId(id));
    }
    /**
     * es 根据商品id差分类
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> findByIds(@RequestParam(name = "ids")List<Long> ids){
        Collection<TbCategory> tbCategories=tbCategoryService.listByIds(ids);
        if (CollectionUtils.isEmpty(tbCategories)){
            throw new LyException(ExcptionEnum.CATEGORY_NOT_FOUND);
        }
        List<CategoryDTO> list=new ArrayList<>();
        for (TbCategory tbCategory : tbCategories) {
            CategoryDTO categoryDTO = BeanHelper.copyProperties(tbCategory, CategoryDTO.class);
            list.add(categoryDTO);
        }
        return ResponseEntity.ok(list);
    }
}
