package com.leyou.page.service;

import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: TianTian
 * @Date: 2020/5/8 8:59
 */
@Service
@Slf4j
public class PageService {


    @Resource
    private ItemClient itemClient;

    @Resource
    private SpringTemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;
    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;

    public void createItemHtml(Long id) {
        // 上下文，准备模型数据
        Context context = new Context();
        // 调用之前写好的加载数据方法
        context.setVariables(loadItemData(id));
        // 准备文件路径
        File dir = new File(itemDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // 创建失败，抛出异常
                log.error("【静态页服务】创建静态页目录失败，目录地址：{}", dir.getAbsolutePath());
                throw new LyException(ExcptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
        File filePath = new File(dir, id + ".html");
        // 准备输出流
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            templateEngine.process(itemTemplate, context, writer);
        } catch (IOException e) {
            log.error("【静态页服务】静态页生成失败，商品id：{}", id, e);
            throw new LyException(ExcptionEnum.FILE_WRITER_ERROR);
        }
    }

    /**
     * 获取模板页面需要的动态数据
     * @param id
     * @return
     */
    public Map<String, Object> loadItemData(Long id) {
        //根据spuid查询Tbspu
        SpuDTO spu = itemClient.findSpuById(id);
        //根据Tbspu中的分类ids,查询分类集合
        //  修改一下根据cids查询分类列表的函数名称吧,findByIds不方便识别
        List<CategoryDTO> categories = itemClient.findByIds(spu.getCategoryIds());
        //根据Tbspu中的品牌id,查询品牌
        //  修改一下根据bid查询品牌的函数名称吧,findByIds不方便识别
        BrandDTO brand = itemClient.findById(spu.getBrandId());

        //根据Tbspu中的分类cid3,查询规格组合规格
        List<SpecGroupDTO> specs = itemClient.findSpecsByCid(spu.getCid3());

        //获取spu下的sku集合
        List<SkuDTO> skuDTOList = itemClient.findSkuBySpuId(id);
        //获取spu detail
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailById(id);
        //封装数据到Map<String,object> 集合data中
        Map<String,Object> data=new HashMap<>();
        //为data封装所需数据
        data.put("categories", categories);
        data.put("brand", brand);
        data.put("spuName", spu.getName());
        data.put("subTitle", spu.getSubTitle());
        data.put("detail", spuDetailDTO);
        data.put("skus", skuDTOList);
//        data.put("detail", spu.getSpuDetail());//不要直接用种方式,有问题
//        data.put("skus", spu.getSkus());//不要直接用这种方式,有问题
        data.put("specs", specs);
        //返回data
        return data;
    }

    public void deleteItemHtml(Long id) {
        File file = new File(itemDir, id + ".html");
        if(file.exists()){
            if (!file.delete()) {
                log.error("【静态页服务】静态页删除失败，商品id：{}", id);
                throw new LyException(ExcptionEnum.FILE_WRITER_ERROR);
            }
        }
    }
}
