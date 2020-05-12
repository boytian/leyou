package com.leyou.item.mapper;

import com.leyou.item.entity.TbCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {

    @Select("SELECT a.* FROM tb_category a , tb_category_brand  b WHERE a.id = b.category_id AND b.brand_id=#{brandId}")
    List<TbCategory> selectCategoryByBrandId(@Param("brandId") Long brandId);
}
