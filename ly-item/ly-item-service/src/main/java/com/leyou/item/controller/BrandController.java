package com.leyou.item.controller;

import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.service.TbBrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: TianTian
 * @Date: 2020/4/22 19:36
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Resource
    private TbBrandService brandService;


    //http://api.leyou.com/api/item/brand/page?
    // key=是否有搜索
    // page=当前页
    // rows=页面大小
    // sortBy= 排序根据规则id或者letter
    // desc=false  排序规则
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(@RequestParam(name = "key",required = false)String key,
                                                                @RequestParam(name = "page",defaultValue = "1")Integer page,
                                                                @RequestParam(name = "rows",defaultValue = "10")Integer rows,
                                                                @RequestParam(name = "sortBy",required = false)String sortBy,
                                                                @RequestParam(name = "desc",defaultValue = "false")Boolean desc){
        return ResponseEntity.ok(brandService.findBrandByPage(key,page,rows,sortBy,desc));
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(TbBrand brand, @RequestParam(name = "cids") List<Long> ids){
        brandService.saveBrand(brand,ids);//保存品牌和分类及品牌的中间表
        return ResponseEntity.status(HttpStatus.CREATED).build();//创建数据
    }

    /**
     * 修改品牌
     * @param brand
     * @param ids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(TbBrand brand,@RequestParam(name = "cids")List<Long> ids){
        brandService.updateBrand(brand,ids);
        return ResponseEntity.noContent().build();//修改后,无返回值时
    }

    @GetMapping("/of/category")
    public  ResponseEntity<List<BrandDTO>> findBrandByCategoryId(@RequestParam(name = "id") Long id){

        return ResponseEntity.ok(brandService.findBrandByCategoryId(id));
    }
    @GetMapping("{id}")
    public  ResponseEntity<BrandDTO> findById(@PathVariable(name = "id") Long id){
        TbBrand byId = brandService.getById(id);
        if (byId==null){
            throw new LyException(ExcptionEnum.CATEGORY_NOT_FOUND);
        }
        BrandDTO brandDTO = BeanHelper.copyProperties(byId, BrandDTO.class);
        return ResponseEntity.ok(brandDTO);

    }

    /**
     * 根据品牌id批量查询品牌
     * @param idList 品牌id的集合
     * @return 品牌的集合
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandsByIds(@RequestParam("ids") List<Long> idList){
        Collection<TbBrand> tbBrands = brandService.listByIds(idList);
        if (CollectionUtils.isEmpty(tbBrands)) {
            throw new LyException(ExcptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> collect = tbBrands.stream().map(tbBrand -> {
            return BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }
}
