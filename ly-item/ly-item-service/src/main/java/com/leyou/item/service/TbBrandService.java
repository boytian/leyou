package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
public interface TbBrandService extends IService<TbBrand> {

    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(TbBrand brand, List<Long> ids);

    void updateBrand(TbBrand brand, List<Long> ids);

    List<BrandDTO> findBrandByCategoryId(Long id);
}
