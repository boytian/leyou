package com.leyou.item.service;

import com.leyou.item.entity.TbCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
public interface TbCategoryService extends IService<TbCategory> {

}
