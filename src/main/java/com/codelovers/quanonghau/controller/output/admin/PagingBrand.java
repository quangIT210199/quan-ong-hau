package com.codelovers.quanonghau.controller.output.admin;

import com.codelovers.quanonghau.models.Brand;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingBrand {
    private int currentPage;
    private int totalPage;
    private long startCount;
    private long endCount;
    private long totalItem;
    private String sortField;
    private String sortDir;
    private String keyword;
    private String reverseSortDir;
    private List<Brand> listBrand = new ArrayList<>();
}
