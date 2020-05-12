package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @version 1.0
 * @Author heima
 **/
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
