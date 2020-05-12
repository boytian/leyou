package com.leyou.search.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author heima
 **/
@RestController
public class SearchController {
    @Resource
    private SearchService searchService;

    //先准备GoodsDTO

    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> search(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.search(request));
    }

    //做过滤条件的聚合
    @PostMapping("/filter")
    public ResponseEntity<Map<String, List<?>>> queryFilter(@RequestBody SearchRequest request)
    {
        return ResponseEntity.ok(searchService.queryFilter(request));
    }

}
