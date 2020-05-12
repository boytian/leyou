package com.leyou.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private List<T> items; // 返回结果
    private Long total; //总条数
    private Long totalPage; //总页数
}