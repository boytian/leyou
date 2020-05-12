package com.leyou.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class SpuDTO {
    /**
     * spu id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题，一般是促销信息
     */
    private String subTitle;

    /**
     * 1级类目id
     */
    private Long cid1;

    /**
     * 2级类目id
     */
    private Long cid2;
    /**
     * 3级类目id
     */
    private Long cid3;

    /**
     * 商品所属品牌id
     */
    private Long brandId;
    /**
     * 是否上架，0下架，1上架
     */
    private Boolean saleable;
    /**
     * 创建时间
     */
    private Date createTime;
    private String brandName;

    /**
     * 分类 名称拼接
     */
    private String categoryName;
    /**
     * sku列表
     */
    private List<SkuDTO> skus;
    /**
     * 商品详情
     */
    private SpuDetailDTO spuDetail;

    @JsonIgnore
    public List<Long> getCategoryIds() {
        return Arrays.asList(cid1, cid2, cid3);
    }
}