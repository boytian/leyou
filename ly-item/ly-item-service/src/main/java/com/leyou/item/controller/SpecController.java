package com.leyou.item.controller;



import com.leyou.item.dto.SpecGroupDTO;


import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.service.SpecService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 * @version 1.0

 * @Author heima

 **/

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Resource
    private SpecService specService;


    /**

     * 查询规格组信息

     * 根据分类id

     * @param id

     * @return

     */

    @GetMapping("/groups/of/category")

    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupByCategoryId(@RequestParam(name = "id") Long id){

        //调用specService的findSpecGroupByCategoryId方法,获取返回值List<SpecGroupDTO>

        List<SpecGroupDTO> groupDTOList = specService.findSpecGroupByCategoryId(id);

        //List<SpecGroupDTO>放入ResponseEntity返回

        return  ResponseEntity.ok(groupDTOList);

    }

    @PostMapping("/group")
    public ResponseEntity<Void> saveBrand(@RequestBody TbSpecGroup tbSpecGroup){
        specService.saveBrand(tbSpecGroup);//保存分组
        return ResponseEntity.status(HttpStatus.CREATED).build();//创建数据
    }

    //http://api.leyou.com/api/item/spec/params?gid=1
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParamByCategoryId(@RequestParam(name = "gid",required = false) Long gid,
                                                                        @RequestParam(name = "cid",required = false) Long cid,
                                                                        @RequestParam(name = "searching",required = false) Boolean searching){

        //调用specService的findSpecGroupByCategoryId方法,获取返回值List<SpecGroupDTO>

        //List<SpecParamDTO> paramDTOList = specService.findSpecParamByCategoryId(gid);
        List<SpecParamDTO> paramDTOList = specService.findSpecParam(gid,cid,searching);

        //List<SpecGroupDTO>放入ResponseEntity返回

        return  ResponseEntity.ok(paramDTOList);

    }
    /**
     * 查询规格参数组，及组内参数
     * @param id 商品分类id
     * @return 规格组及组内参数
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecsByCid(@RequestParam("id") Long id){
        return ResponseEntity.ok(specService.findSpecsByCid(id));
    }


}