package com.codelovers.quanonghau.controller.output;

import com.codelovers.quanonghau.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingCategories {
    private long startCount;
    private long endCount;
    private int totalPages;
    private long totalItems;
    private int currentPage;
    private String reverseSortDir;
    private String sortField;
    private String sortDir;
    private String keyword;
    private List<Category> categoryList = new ArrayList<>();
}
