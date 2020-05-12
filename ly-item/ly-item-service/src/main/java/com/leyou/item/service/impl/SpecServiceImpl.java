package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.service.SpecService;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: TianTian
 * @Date: 2020/4/26 17:06
 */
@Service
public class SpecServiceImpl implements SpecService {

    @Resource
    private TbSpecGroupService tbSpecGroupService;
    @Resource
    private TbSpecParamService tbSpecParamService;

    @Override
    public List<SpecGroupDTO> findSpecGroupByCategoryId(Long cid) {

        //select * from tb_spec_group where cid=76
        //生成where条件狗狗构造器
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid",cid);
        List<TbSpecGroup> list = tbSpecGroupService.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            throw new LyException(ExcptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(list, SpecGroupDTO.class);
        return specGroupDTOS;
    }

    @Override
    public void saveBrand(TbSpecGroup tbSpecGroup) {
        boolean save = tbSpecGroupService.save(tbSpecGroup);
        if (!save){
            throw new LyException(ExcptionEnum.SPEC_NOT_SaveFOUND);
        }
    }

    //#根据规格组查参数
    //SELECT * FROM tb_spec_param WHERE group_id=1
    @Override
    public List<SpecParamDTO> findSpecParamByCategoryId(Long gid) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",gid);
        List<TbSpecParam> list = tbSpecParamService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExcptionEnum.SPEC_NOT_FOUND);
        }
        List<SpecParamDTO> specParamDTOS = BeanHelper.copyWithCollection(list, SpecParamDTO.class);
        return specParamDTOS;
    }

    @Override
    public List<SpecParamDTO> findSpecParam(Long gid, Long cid, Boolean searching) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        if (gid!=null && gid!=0){
            queryWrapper.eq("group_id",gid);
        }
        if (cid!=null && cid!=0){
            queryWrapper.eq("cid",cid);
        }
        if (searching!=null){
            queryWrapper.eq("searching",searching);
        }
        List<TbSpecParam> list = tbSpecParamService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExcptionEnum.SPEC_NOT_FOUND);
        }
        List<SpecParamDTO> specParamDTOS = BeanHelper.copyWithCollection(list, SpecParamDTO.class);
        return specParamDTOS;
    }

    public List<SpecGroupDTO> findSpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroupDTO> groupList = findSpecGroupByCategoryId(cid);
        // 查询分类下所有规格参数
        List<SpecParamDTO> params = findSpecParam(null,cid,null);
        // 将规格参数按照groupId进行分组，得到每个group下的param的集合
        Map<Long, List<SpecParamDTO>> paramMap = params.stream()
                .collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        // 填写到group中
        for (SpecGroupDTO groupDTO : groupList) {
            groupDTO.setParams(paramMap.get(groupDTO.getId()));
        }
        return groupList;
    }


}
