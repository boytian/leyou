package com.leyou.item.service;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbSpecGroup;

import java.util.List;

/**
 * @Author: TianTian
 * @Date: 2020/4/26 17:05
 */
public interface SpecService {
    List<SpecGroupDTO> findSpecGroupByCategoryId(Long id);


    void saveBrand(TbSpecGroup tbSpecGroup);

    List<SpecParamDTO> findSpecParamByCategoryId(Long gid);

    List<SpecParamDTO> findSpecParam(Long gid, Long cid, Boolean searching);

    List<SpecGroupDTO> findSpecsByCid(Long id);
}
