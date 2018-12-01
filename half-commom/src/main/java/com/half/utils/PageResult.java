package com.half.utils;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    //当前页数
    private Integer page;
    //总页数
    private Integer totalPage;
    //总记录数
    private Long totalElements;
    //每页的数据结果
    private List<?> rows;
}
