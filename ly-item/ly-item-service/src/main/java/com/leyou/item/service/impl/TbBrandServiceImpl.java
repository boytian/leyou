package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.service.TbCategoryBrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-04-19
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {


    @Resource
    private TbCategoryBrandService categoryBrandService;//用于保存品牌和分类对照表

    @Resource
    private  TbBrandMapper tbBrandMapper;

    /**
     *
     * @param key  模糊查询
     * @param page  当前页
     * @param rows   页面大小
     * @param sortBy  排序规则
     * @param desc  升序降序
     * @return
     */
    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        //      SELECT * FROM tb_brand WHERE NAME LIKE '%华%' OR letter LIKE '%华%' order by sortBy desc
//        构造分页的2个参数
        IPage<TbBrand> page1 = new Page<>(page,rows);
//         构造查询条件
        QueryWrapper<TbBrand> queryWrapper = new QueryWrapper<>();
//        添加条件
        if(!StringUtils.isBlank(key)){
            queryWrapper.like("name",key).or().like("letter",key);
          //  queryWrapper.lambda().like(TbBrand::getName,key).or().like(TbBrand::getLetter,key);
        }
        if(!StringUtils.isBlank(sortBy)){
//            添加排序
            if(desc){
                queryWrapper.orderByDesc(sortBy);
            }else{
                queryWrapper.orderByAsc(sortBy);
            }
        }
//      分页查询
        IPage<TbBrand> brandIPage = this.page(page1, queryWrapper);
        List<TbBrand> tbBrandList = brandIPage.getRecords();
        if(CollectionUtils.isEmpty(tbBrandList)){
            throw new LyException(ExcptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(tbBrandList, BrandDTO.class);
        return new PageResult<>(brandDTOList,brandIPage.getTotal(),brandIPage.getPages());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(TbBrand brand, List<Long> ids) {
//        保存品牌表
        boolean b = this.save(brand);
        if(!b){
            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
        }
//        获取品牌id
        Long brandId = brand.getId();
//        保存中间表

        List<TbCategoryBrand> categoryBrandList = new ArrayList<>();
        for (Long categoryId : ids) {
            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
            tbCategoryBrand.setBrandId(brandId);
            tbCategoryBrand.setCategoryId(categoryId);
            categoryBrandList.add(tbCategoryBrand);
        }
        boolean b1 = categoryBrandService.saveBatch(categoryBrandList);//批量保存数据
        if(!b1){//如果保存不成功,抛出保存操作异常
            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(TbBrand brand, List<Long> ids) {

        Long brandId = brand.getId();

//      修改品牌表

        boolean b = this.updateById(brand);

        if(!b){

            throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);

        }

//        删除中间表数据

//        delete from tb_category_brand where brand_id=?'

        QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("brand_id",brandId);

        boolean b1 = categoryBrandService.remove(queryWrapper);

        if(!b1){

            throw new LyException(ExcptionEnum.DELETE_OPERATION_FAIL);

        }

//        新增中间表数据

        List<TbCategoryBrand> categoryBrandList = new ArrayList<>();

        for (Long categoryId : ids) {//遍历categoryIds,

            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();

            tbCategoryBrand.setCategoryId(categoryId);

            tbCategoryBrand.setBrandId(brandId);

            categoryBrandList.add(tbCategoryBrand);

        }

        boolean b2 = categoryBrandService.saveBatch(categoryBrandList);

        if(!b2){

            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);

        }

    }

    @Override
    public List<BrandDTO> findBrandByCategoryId(Long cid) {
        //根据cid查询对应的品牌
        List<TbBrand> tbBrands = this.tbBrandMapper.selectBrandListByCid(cid);
        if (CollectionUtils.isEmpty(tbBrands)){
            throw new LyException(ExcptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOS = BeanHelper.copyWithCollection(tbBrands, BrandDTO.class);
        return brandDTOS;
    }
}
