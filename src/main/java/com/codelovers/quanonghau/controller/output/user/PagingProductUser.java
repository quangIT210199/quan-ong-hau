package com.codelovers.quanonghau.controller.output.user;

import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.models.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingProductUser {
    private int currentPage;
    private int totalPages;
    private long startCount;
    private long endCount;
    private long totalItems;
    private String pageTitle;
    private List<Category> listCategoryParents = new ArrayList<>();
    private List<Product> listProductByCateId = new ArrayList<>();
    private Category category;
}
