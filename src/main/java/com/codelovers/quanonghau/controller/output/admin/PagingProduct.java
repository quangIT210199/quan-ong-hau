package com.codelovers.quanonghau.controller.output.admin;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingProduct {
    private int categoryID;
    private int currentPage;
    private int totalPages;
    private long startCount;
    private long endCount;
    private long totalItems;
    private String sortField;
    private String sortDir;
    private String keyword;
    private String reverseSortDir;
    private List<Product> productList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
}
