package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author heima
 **/
@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;
    //tbspu 直接跟数据库相关pojo,
    public Goods buildGoods(SpuDTO spuDTO){
        //1.商品的spuId 搜索相关  分类名称(all),品牌名称(all),skus(price),规格信息(specs) 做相应的处理
        //1.1 all 处理: 查找品牌名称, 查找分类的名称 , 品牌名+分类名+subtiele 做拼接
        //1.1.1 查询分类名称//categoryDTOS遍历,取名称,将名称拼接成一个字符串
        List<CategoryDTO> categoryDTOS = itemClient.findByIds(spuDTO.getCategoryIds());
        String categoryNames = categoryDTOS.stream().map(CategoryDTO::getName).collect(Collectors.joining(","));//手机,手机通讯,手机
        //1.1.2 查询品牌名称
        BrandDTO brandDTO = itemClient.findById(spuDTO.getBrandId());
        //1.1.3 品牌名+分类名+subtiele 做拼接 结果为:"all": "小米（MI） 小米5X 全网通4G智能手机 ,手机,手机通讯,手机,小米（MI）"
        String all=spuDTO.getSubTitle()+","+categoryNames+","+brandDTO.getName();


        //2. 求skus , 求spu下的skus  转化为json数组
        //2.1 根据spuId 求skus
        List<SkuDTO> skuList = itemClient.findSkuBySpuId(spuDTO.getId());
        //2.2 遍历skuList,取出每一个SkuDTO其中的id,price,image,title-->Map<String,object>
        ArrayList<Map<String, Object>> skuMap = new ArrayList<Map<String, Object>>();
        for (SkuDTO skuDTO : skuList) {
            Map<String, Object> map = new HashMap<>();
            //分别取四个四段,放入map中
            map.put("id", skuDTO.getId());
            map.put("price", skuDTO.getPrice());
            map.put("title", skuDTO.getTitle());
            //如果包含的是多个图片,需要取第一个图片"http://image.leyou.com/images/4/8/1524297461644.jpg,http://image.leyou.com/images/4/8/1524297461644.jpg"
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
            skuMap.add(map);
        }

        //3.处理spu下的所有sku价格的集合,set<Long> 不能重复
        Set<Long> price = skuList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());

        //4.构造spu的规格参数
        Map<String, Object> specs = new HashMap<>();
        //4.1 获取规格参数的key,
        List<SpecParamDTO> specParams = itemClient.findSpecParam(null, spuDTO.getCid3(), true);
        //4.2 取spuDetail中的数据,
        SpuDetailDTO spuDetail = itemClient.findSpuDetailById(spuDTO.getId());
        //4.2.1 取通用规格参数的值,并将之转化为Map类型
        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, Object.class);
        //4.2.1 取特殊规格参数的值,转化为Map<Long,List<String>>
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        //4.3将specParams做遍历,匹配通过规格和特殊规格, 数字类型, 生成新的Map集合
        for (SpecParamDTO specParam : specParams) {
            //获取规格参数的名称
            String key = specParam.getName();
            //获取规格参数的值 ()
            Object value=null;//存储特殊规格或者通用规格的值
            //如果是通用规格,则需要匹配spuDetail中的通用属性对应的值
            if (specParam.getGeneric()) {
                value = genericSpec.get(specParam.getId());
            }else{
                //否则就是特殊规格
                value = specialSpec.get(specParam.getId());
            }

            //数字类型的规格处理,
            if (specParam.getIsNumeric()) {
                //是数字类型的,处理分段和单位的拼接
                //定义一个函数
                value=chooseSegment(value,specParam);
            }

            specs.put(key, value);

        }


        Goods goods = new Goods();
        goods.setId(spuDTO.getId());//
        goods.setSubTitle(spuDTO.getSubTitle());//
        goods.setSkus(JsonUtils.toString(skuMap));//?需要根据spuId,查skus,需要调用itemClicet的findSkuBySpuId(spuId)去查询
        goods.setAll(all);// 需要我们先查询出分类的名称,品牌的名称,将拼接"分类名,品牌,spbTitle"
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCategoryId(spuDTO.getCid3());//选第三级分类的id
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        goods.setPrice(price);// 首先根据spuId传skus,然后依次取出价格,价格去重后,保存到Set<Long> 集合中
        goods.setSpecs(specs);// 首先需要从spuDetail中找到对应的special_spec 和 generic_spec取出后,作拼接
        return goods;
    }

    public Object chooseSegment(Object value,SpecParamDTO p){
        String result="其他";

        //判断是否为空或者空字符串
        if (value==null || StringUtils.isBlank(value.toString())) {
            return "其他";
        }

        //如果不为空,需要将value转化为double类型
        //todo  特殊处理  将字符串处理成double类型
        double val=parseDouble(value.toString());
        //保存数值端,
//         p.getSegments().split(",");  0-200,200-500,500-1000,1000-1500,1500-
        //                              200万以下, 200-500万,500-1000万,1000-1500万, 1500万以上
        for(String segment:p.getSegments().split(",")){
            String[] segs = segment.split("-");
            //将segs中的两个字符串转化为数字,end有可能 2500+-->double.Max_value
            double begin = parseDouble(segs[0]);
            double end =Double.MAX_VALUE;
            //如果segs有两个元素
            if (segs.length==2) {
                end= parseDouble(segs[1]);
            }

            //判断是否在给出的范围内
            if (val>=begin && val<end) {
                if (segs.length==1) {
                    result= segs[0] + p.getUnit() + "以上";
                }else if(begin==0){
                    result=segs[1]+p.getUnit()+"以下";
                }else{
                    result=segment+p.getUnit();
                }
                break;
            }
        }

        return result;

    }

    private double parseDouble(String str){
        try {

            return Double.parseDouble(str);
        }catch (Exception e){
            return 0;
        }
    }

    public PageResult<GoodsDTO> search(SearchRequest request) {
        //1.取出关键字
        String key = request.getKey();
        // 判断关键字是否为空,为空抛异常
        if (StringUtils.isBlank(key)) {
            throw new LyException(ExcptionEnum.INVALID_PARAM_ERROR);
        }
        //2. 创建原生的构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //3.设置过滤条件 source
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{
                "id","subTitle","skus"}, null));
        //4.搜索条件, 模糊 match
        QueryBuilder baseQuery = buildBaseQuery(request);
        queryBuilder.withQuery(baseQuery);
//        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
        //构建布尔查询

        //5.分页条件设置
        Integer page = request.getPage()-1;
        Integer size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page,size ));

        //6.获取分页查询的结果
        AggregatedPage<Goods> goods = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //7.从查询结果中得到total 和totalpage
        int totalPages = goods.getTotalPages();
        long total = goods.getTotalElements();
        List<Goods> goodsList = goods.getContent();
        //8.获取结果集List<GoodsDTO>
        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);
        //9.封装pageResult,并返回
        return new PageResult<>(goodsDTOS,total,Long.valueOf(totalPages));
    }

    //聚合查询的条件

    public Map<String, List<?>> queryFilter(SearchRequest request) {
        /**
         * 7.从搜索的结果集中做聚合
         * 8.做brandId的聚合-->bids
         * 9.做CtegoryId的聚合-->cids
         * 10.根据bids-->调用接口查询品牌列表List<TbBrand>-->List<BrandDTO>
         * 11.根据cids-->调用接口查询分类列表List<TbCategory>-->List<CategoryDTO>
         * 12.将List<BrandDTO>和List<CategoryDTO>放到resultMap<String,List<?>>
         */
        //1.创建聚合过滤条件的集合
        Map<String, List<?>> filterResult = new LinkedHashMap<>();

        //2.查询条件的设置
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //3. 这只过滤的字段
        QueryBuilder baseQuery = buildBaseQuery(request);
        queryBuilder.withQuery(baseQuery);
        //检索的结果值需要聚合的结果
        //4. 分页设置
        queryBuilder.withPageable(PageRequest.of(0, 1));
        //聚合结果不用,显示可以不设置
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        //5.生成分类聚合
        String categoryAgg = "categoryAggs";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAgg).field("categoryId"));
        //6.生成品牌聚合
        String brandAgg = "brandAggs";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));
        //7查询es 返回结果
        AggregatedPage<Goods> aggregat = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //8通过分类聚合cids-->调用接口查询分类列表List<TbCategory>-->List<CategoryDTO>-->放入结果集
        Aggregations aggregations = aggregat.getAggregations();
        LongTerms categoryTerms = aggregations.get(categoryAgg);
        //生成处理分类条件的函数
        //获取分类的idList
        List<Long> idList=handlerCategoryAgg(categoryTerms,filterResult);
        //9通过品牌聚合bids-->调用接口查询分类列表List<TbBrand>-->List<BrandDTO>-->放入结果集

        //生成品牌条件函数处理
        LongTerms brandTerms = aggregations.get(brandAgg);
        handlerBrandAgg(brandTerms,filterResult);

        //判断分类数量是否>1,如果是,不聚合,==1时聚合
        if (idList!=null && idList.size()==1) {
            //此时才聚合规格参数,cid
            //需要做查找,需要引入baseQuery,
            //找到的结果,放入filterResult
            handlerSpecAgg(idList.get(0),baseQuery,filterResult);
        }

        return filterResult;
    }

    /**
     * 根据cid提取对应的规格参数,用户搜索的过滤条件
     * @param cid
     * @param baseQuery
     * @param filterResult
     */
    private void handlerSpecAgg(Long cid, QueryBuilder baseQuery, Map<String, List<?>> filterResult) {
        //1.通过cid查找规格参数列表,searching=true ,用于检索用的
        List<SpecParamDTO> specParams = itemClient.findSpecParam(null, cid, true);
        //2做es的检索,检索的结果作为聚合的前提
        //设置查询条件
        //2.1
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(baseQuery);
        //只需要聚合的结果,显示时只显示1条
        queryBuilder.withPageable(PageRequest.of(0, 1));
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());

        //3.提取聚合条件,拼接成类似于specs.前置摄像头
        for (SpecParamDTO specParam : specParams) {
            String name = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name));
        }

        AggregatedPage<Goods> result = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggregations = result.getAggregations();

        //4.解析聚合的结果,放入filterResult
        for (SpecParamDTO specParam : specParams) {
            //获取specParam.name,作为聚合名称
            String name=specParam.getName();
            StringTerms terms=aggregations.get(name);
            //获取聚合的结果,结果是字符串,
            List<String> value = terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString)
                    //过滤控制
                    .filter(StringUtils::isNotEmpty)
                    //收集记过
                    .collect(Collectors.toList());
            //存入map中
            filterResult.put(name,value);
        }

    }

    private void handlerBrandAgg(LongTerms brandTerms, Map<String, List<?>> filterResult) {
        //通过分类聚合bids-->调用接口查询品牌列表-->List<BrandDTO>-->放入结果集
        //1.解析聚合后的bucket,取出cids
        List<Long> bids = brandTerms.getBuckets().stream().map(
                LongTerms.Bucket::getKeyAsNumber).map(Number::longValue)
                .collect(Collectors.toList());
        //2.调用通过bids查找List<BrandDTO>的方法
        List<BrandDTO> brandDTOS = itemClient.findBrandByIds(bids);

        //3.放入filterResult结果集中
        filterResult.put("品牌", brandDTOS);
    }

    private List<Long> handlerCategoryAgg(LongTerms categoryTerms, Map<String, List<?>> filterResult) {
        //通过分类聚合cids-->调用接口查询分类列表-->List<CategoryDTO>-->放入结果集
        //1.解析聚合后的bucket,取出cids
        List<Long> cids = categoryTerms.getBuckets().stream().map(
                LongTerms.Bucket::getKeyAsNumber).map(Number::longValue)
                .collect(Collectors.toList());
        //2.调用通过cids查找List<CategoryDTO>的方法
        List<CategoryDTO> categoryDTOS = itemClient.findByIds(cids);

        //3.放入filterResult结果集中
        filterResult.put("分类", categoryDTOS);
        return cids;
    }

    /**
     * 根据关键字key跟all走match模糊查询,在它的基础上做条件过滤
     * @param request
     * @return
     */
    private QueryBuilder buildBaseQuery(SearchRequest request){
        //在这里面做布尔类型查询的过滤条件

        //构造过滤条件
        Map<String, String> filter = request.getFilter();
        BoolQueryBuilder queryBuilder=QueryBuilders.boolQuery();

        queryBuilder.must( QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //过滤条件的添加
        //如果filter不为空,从fitler中取各种条件,加以过滤
        if (!CollectionUtils.isEmpty(filter)) {
            //遍历取fitler中的各个条件
            for (Map.Entry<String, String> entry : filter.entrySet()) {
                //取key
                String key = entry.getKey();
                if ("分类".equals(key)) {
                    key= "categoryId";
                }else if("品牌".equals(key)){
                    key="brandId";
                }else{
                    key="specs."+key;
                }
                //取value值
                String value = entry.getValue();
                //添加过滤条件
                queryBuilder.filter(QueryBuilders.termQuery(key, value));
            }
        }
        return queryBuilder;
    }

    /**
     * 根据spuid做商品上架存es
     *
     * @param spuId
     */
    public void createIndex(Long spuId) {
        SpuDTO spuById = itemClient.findSpuById(spuId);
        Goods goods = buildGoods(spuById);
        goodsRepository.save(goods);
    }

    public void removeIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
