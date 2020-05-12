package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @version 1.0
 * @Author heima
 **/
@FeignClient("item-service")
public interface ItemClient {

    //•第一：分页查询spu的服务，已经写过。(商品管理查询)
    @GetMapping("/spu/page")
    PageResult<SpuDTO> findSpuByPage(@RequestParam(name = "page",defaultValue = "1")Integer page,
                                     @RequestParam(name = "rows",defaultValue = "5")Integer rows,
                                     @RequestParam(name = "key",required = false)String key,
                                     @RequestParam(name = "saleable",required = false)Boolean saleable);


    //•第二：根据spuId查询skus的服务，已经写过(商品修改回显)
    @GetMapping("/sku/of/spu")
    List<SkuDTO> findSkuBySpuId(@RequestParam(name = "id")Long id);

    //•第三：根据spuId查询SpuDetail的服务，已经写过(商品修改回显)
    @GetMapping("/spu/detail")
    SpuDetailDTO findSpuDetailById(@RequestParam(name = "id")Long id);

    //•第四：根据商品分类ids，查询商品分类列表，没写过。需要一个根据多级分类id查询分类的接口
    @GetMapping("/category/list")
    List<CategoryDTO> findByIds(@RequestParam(name = "ids") List<Long> ids);


    //•第五：查询分类id下可以用来搜索的规格参数：写过(规格管理 )
    @GetMapping("/spec/params")
    List<SpecParamDTO>  findSpecParam(@RequestParam(value = "gid",required = false)Long gid,
                                      @RequestParam(value="cid",required = false)Long cid,
                                      @RequestParam(value="searching",required = false)Boolean searching);
    //•第六：根据品牌id查询品牌，没写过
    @GetMapping("/brand/{id}")// es restful
    BrandDTO findById(@PathVariable("id") Long id);


    @GetMapping("/brand/list")
    List<BrandDTO> findBrandByIds(@RequestParam(name = "ids") List<Long> ids);


    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    SpuDTO findSpuById(@PathVariable("id") Long id);

    /**
     * 查询规格参数组，及组内参数
     * @param id 商品分类id
     * @return 规格组及组内参数
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> findSpecsByCid(@RequestParam("id") Long id);

}
