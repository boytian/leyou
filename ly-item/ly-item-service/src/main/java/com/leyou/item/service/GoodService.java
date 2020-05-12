package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.TbSpuMapper;
import com.leyou.item.service.impl.TbSpuServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * @Author: TianTian
 * @Date: 2020/4/26 19:43
 */
@Service
public class GoodService{
    @Resource
    private TbSpuServiceImpl tbSpuService;
    @Resource
    private TbSkuService tbSkuService;
    @Resource
    private TbCategoryService tbCategoryService;
    @Resource
    private TbBrandService tbBrandService;
    @Resource
    private TbSpuDetailService tbSpuDetailService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;


    //添加商品的分页查找
    public PageResult<SpuDTO> findSpuByPage(Integer page,Integer rows, String key,Boolean saleable){
        //SELECT * from tb_spu where saleable=1 ORDER BY update_time DESC LIMIT 0,5;
        //#key 值
        //SELECT * from tb_spu where saleable=1  and name like "华为%" ORDER BY update_time DESC LIMIT 0,5;
        //1.拼接sql语句,分页,是否上架,模糊检索,排序
        //1.1 构造分页信息
        Page<TbSpu> spuPage = new Page<>(page, rows);
        //1.2 生成条件构造器
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        //1.3 是否上架
        if (saleable!=null) {
            queryWrapper.eq("saleable",saleable);
        }
        //1.4 是否有key
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.like("name",key);
        }
        //1.5 倒排序
        queryWrapper.orderByDesc("update_time");

        //2.分页查询
        IPage<TbSpu> spuIPage = tbSpuService.page(spuPage, queryWrapper);

        //3.取出分页中的集合list,如果集合为空,则抛异常
        if ((spuIPage==null || CollectionUtils.isEmpty(spuIPage.getRecords()))) {
            throw new LyException(ExcptionEnum.GOODS_NOT_FOUND);
        }


        //4.如果集合list不为空,做转化.-->list<SpuDTO>
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spuIPage.getRecords(), SpuDTO.class);
        //todo 处理分类名称和品牌名称
        this.handleBrandAndCateGoryName(spuDTOS);
        //5.生成一个分页PageResult,并给他赋值total,totalpage,list<SpuDTO>,并返回
        return new PageResult<>(spuDTOS,spuIPage.getTotal(),spuIPage.getPages());
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void  saveGoods(SpuDTO spuDTO) {
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean save = tbSpuService.save(tbSpu);
        //判断是否成功
        if (!save){
            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存detail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetail, TbSpuDetail.class);
        tbSpuDetail.setSpuId(tbSpu.getId());
        boolean save1 = tbSpuDetailService.save(tbSpuDetail);
        if (!save1){
            throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存skus
        List<SkuDTO> skus = spuDTO.getSkus();
        if (!CollectionUtils.isEmpty(skus)){
            //不为空则保存，遍历转换
            List<TbSku> skuList=new ArrayList<>();
            for (SkuDTO skuDTO : skus) {
                skuDTO.setSpuId(tbSpu.getId());
                TbSku tbSku = BeanHelper.copyProperties(skuDTO, TbSku.class);

                skuList.add(tbSku);
            }
            System.out.println(skuList.toString());
            boolean b = tbSkuService.saveBatch(skuList);
            if (!b){
                throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
            }
        }

    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsSaleable(Long id, Boolean saleable) {
        TbSpu tbspu=new TbSpu();
        tbspu.setId(id);
        tbspu.setSaleable(saleable);
        boolean b = tbSpuService.updateById(tbspu);
        if (!b){
            throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);
        }
        UpdateWrapper<TbSku> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("spu_id",id);
        updateWrapper.set("enable",saleable);
        boolean update = tbSkuService.update(updateWrapper);
        if (!update){
            throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);
        }
        //上架或者下架通知
        String tag=saleable?ITEM_UP_TAGS:ITEM_DOWN_TAGS;
        rocketMQTemplate.convertAndSend(ITEM_TOPIC_NAME+":"+tag,id);
    }

    
    public SpuDetailDTO findSpuDetailById(Long id) {
        QueryWrapper<TbSpuDetail> queryWrapper = new QueryWrapper();
        queryWrapper.eq("spu_id",id);
        TbSpuDetail tbSpuDetail = tbSpuDetailService.getOne(queryWrapper);
        if (tbSpuDetail==null){
            throw  new LyException(ExcptionEnum.GOODS_NOT_FOUND);
        }
        SpuDetailDTO spuDetailDTO = BeanHelper.copyProperties(tbSpuDetail, SpuDetailDTO.class);
        return spuDetailDTO;
    }

    
    public List<SkuDTO> findSkuBySpuId(Long id) {
        //根据spuId查询所有的sku
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper();
        queryWrapper.eq("spu_id",id);
        List<TbSku> list = tbSkuService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExcptionEnum.GOODS_NOT_FOUND);
        }
        List<SkuDTO> skuDTOs = BeanHelper.copyWithCollection(list, SkuDTO.class);
        return skuDTOs;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuDTO spuDTO) {
        //1.修改tbspu的信息
        //转换dto
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        //保存
        boolean b = tbSpuService.updateById(tbSpu);
        if (!b){
             throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);
        }
        //2、修改spudetail的信息
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), TbSpuDetail.class);
        //3、修改tbsku集合
        UpdateWrapper<TbSpuDetail> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("spu_id",tbSpuDetail.getSpuId());
        boolean b1 = tbSpuDetailService.update(tbSpuDetail,updateWrapper);
        if (!b1){
            throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);
        }
        //首先删除原来的
        //取出来根据spuid
        Long id = spuDTO.getId();
        //删除拼接条件
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper();
        queryWrapper.eq("spu_id",id);
        boolean b3 = tbSkuService.remove(queryWrapper);
        if (!b3){
            throw new LyException(ExcptionEnum.DELETE_OPERATION_FAIL);
        }
        //保存skus
        List<SkuDTO> skus = spuDTO.getSkus();
        if (!CollectionUtils.isEmpty(skus)){
            //不为空则保存，遍历转换
            List<TbSku> skuList=new ArrayList<>();
            for (SkuDTO skuDTO : skus) {
                skuDTO.setSpuId(tbSpu.getId());
                TbSku tbSku = BeanHelper.copyProperties(skuDTO, TbSku.class);

                skuList.add(tbSku);
            }
            boolean b4 = tbSkuService.saveBatch(skuList);
            if (!b4){
                throw new LyException(ExcptionEnum.INSERT_OPERATION_FAIL);
            }
        }
    }


    private void handleBrandAndCateGoryName(List<SpuDTO> spuDTO) {
        //遍历
        for (SpuDTO dto : spuDTO) {
            List<Long> categoryIds = dto.getCategoryIds();
            //通过categoryIds拼接1，2，3
            Collection<TbCategory> tbCategories = tbCategoryService.listByIds(categoryIds);
            String collect = tbCategories.stream().map(TbCategory::getName).collect(Collectors.joining("/"));
            dto.setCategoryName(collect);
            TbBrand byId = tbBrandService.getById(dto.getBrandId());
            dto.setBrandName(byId.getName());
        }
    }

    public SpuDTO findSpuById(Long spuId) {
        TbSpu tbSpu = tbSpuService.getById(spuId);
        if(tbSpu == null){
            throw new LyException(ExcptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpu,SpuDTO.class);
    }
}
