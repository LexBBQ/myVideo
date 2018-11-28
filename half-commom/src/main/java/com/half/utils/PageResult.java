package com.half.utils;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Integer page;
    private Integer totalPage;
    private Long totalElements;
    private List<?> rows;
}
