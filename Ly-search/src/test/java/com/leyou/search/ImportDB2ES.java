package com.leyou.search;

import com.leyou.LySearchApp;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author heima
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApp.class)
public class ImportDB2ES {

    //feign接口引入
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;


    //写一个导入数据的方法

    /**
     * 将数据库中的spu批量导入到es
     */
    @Test
    public void db2Es(){
        //使用itemClient的分页查询去查询
        int page=1;
        int rows =50;

        //循环从数据库中取数据,知道数据全部取出,结束
        //查询出的数据,调用SearchService中的buildGoods方法-->存入一个list集合
        while (true) {
            //每次查询50数据,值查上架的数据,saleable=true
            PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(page, rows, null, true);
            //判断是否查询出数据记录是否为空,只有不为空时,将List<spuDTO>-->List<Goods>
            if (pageResult==null || CollectionUtils.isEmpty(pageResult.getItems())) {
                System.out.println("退出循环1");
                break;  //如果为空,要跳出循环,说明数据已经查完了
            }

            //不为空时,将List<spuDTO>-->List<Goods>
            List<SpuDTO> spuDTOS = pageResult.getItems();
            List<Goods> goodList = new ArrayList<Goods>();

            for (SpuDTO spuDTO : spuDTOS) {

                Goods goods = searchService.buildGoods(spuDTO);
                goodList.add(goods);
            }

            //将goodsList保存到es中
            //调用GoodsRepository的saveAll方法保存到es中
            goodsRepository.saveAll(goodList);

            //如果查询的数据条数<rows,说明已经是最后一页了,也要跳出
            if (pageResult.getItems().size()<rows) {
                System.out.println("退出循环2");
                break;
            }
            //page++,做后续数据的循环
            page++;
        }
        System.out.println("hello");


    }
}
