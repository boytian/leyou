package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.mapper.TbCategoryBrandMapper;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Resource
    private TbCategoryMapper tbCategoryMapper;
    @Override
    public List<CategoryDTO> tbCategoryService(Long pid) {
        //SELECT * FROM tb_category WHERE parent_id=0;
        //构造条件
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",pid);
        List<TbCategory> tbCategories = tbCategoryMapper.selectList(queryWrapper);
        //如果为空抛出一个异常
        if (CollectionUtils.isEmpty(tbCategories)){
            throw new LyException(ExcptionEnum.CATEGORY_NOT_FOUND);
        }
        //否则转化
        List<CategoryDTO> categoryDTOS = BeanHelper.copyWithCollection(tbCategories, CategoryDTO.class);
        return categoryDTOS;
    }
}
