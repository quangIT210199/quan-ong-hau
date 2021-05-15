package com.codelovers.quanonghau.controller.output.user;

import com.codelovers.quanonghau.entity.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PagingSearchProduct {
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private long startCount;
    private long endCount;
    private String keyword;
    private List<Product> listSearchProducts = new ArrayList<>();

}
