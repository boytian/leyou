package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: TianTian
 * @Date: 2020/4/26 20:49
 */
@RestController
public class GoodsController {

@Resource
private GoodService goodService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuPage(
            @RequestParam(name="page",defaultValue = "1")Integer page,
            @RequestParam(name="rows",defaultValue = "5")Integer rows,
            @RequestParam(name="key",required = false)String key,
            @RequestParam(name="saleable",required = false)Boolean saleable
            ){
        return ResponseEntity.ok(goodService.findSpuByPage(page,rows,key,saleable));
    }

    /**
     * 新增商品
     * @param spuDTO 页面提交商品信息  注意：通过@RequestBody注解来接收Json请求
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 商品你下架
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateGoods(@RequestParam(name = "id") Long id,@RequestParam(name = "saleable") Boolean saleable){
        goodService.updateGoodsSaleable(id,saleable);
    return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询spu——detail数据
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findSpuDetailById(@RequestParam(name = "id") Long id){
        return ResponseEntity.ok(goodService.findSpuDetailById(id));
    }

    //sku/of/spu?id=194
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> findSkuBySpuId(@RequestParam(name = "id") Long id){
        return ResponseEntity.ok(goodService.findSkuBySpuId(id));
    }

    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {
        goodService.updateGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodService.findSpuById(id));
    }
}
