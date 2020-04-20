package com.leyou.item.controller;

import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.entity.Item;
import com.leyou.item.entity.TbUser;
import com.leyou.item.service.TbUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: TianTian
 * @Date: 2020/4/19 14:19
 */
@RestController
public class TestMybatisController {

    @Resource
    private TbUserService userService;

    /**
     * 测试逆向工程是否能用
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public TbUser testMybatis( @PathVariable  Long id){
        TbUser user = userService.getById(id);
        return user;
    }

    /**
     * 测试返回
     * @param price
     * @return
     */
    @GetMapping("/itemPrice")
    public Map<String,Object> testitemPrice( Long price){
        //记录状态，结果，返回的信息
        Map<String,Object> resultMap = new HashMap<String,Object>();
        //判断价格是否为空
        if (price==null){
            resultMap.put("result",false);
            resultMap.put("msg","您输入的价格为空，价格不能为空");
            resultMap.put("data",null);
        }else {
            resultMap.put("result",true);
            resultMap.put("msg","您输入的价格没有问题");
            resultMap.put("data",price);
        }

        return resultMap;
    }

    /**
     * 测试返回，字符串
     * 使用ResponseEntity作为响应结果
     */
    @GetMapping("/itemResponse")
    public ResponseEntity<String> test1(Long price){
        if (price==null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("价格不能为空");
        }
        return ResponseEntity.status(HttpStatus.OK).body("价格正确为："+price);
    }
    @PostMapping("/item")
    public ResponseEntity<Item> saveItem(Item item){
        // 如果价格为空，则抛出异常，返回400状态码，请求参数有误
        if(item.getPrice() == null){
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //异常处理替代ResponseEntity发生错误的时候
            throw new LyException(600,"价格不能为空!!!!");
        }
        item.setId(11);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @PostMapping("/itemEnum")
    public ResponseEntity<Item> itemEnum(Item item){
        // 如果价格为空，则抛出异常，返回400状态码，请求参数有误
        if(item.getPrice() == null){
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //异常处理替代ResponseEntity发生错误的时候
            throw new LyException(ExcptionEnum.PRICE_CANNOT_BE_NULL);
        }
        item.setId(11);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }
}
