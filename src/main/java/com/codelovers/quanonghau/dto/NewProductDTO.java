package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class NewProductDTO {
    private Product product;
    private List<Category> categoryList;

    public NewProductDTO(Product product, List<Category> categoryList) {
        this.product = product;
        this.categoryList = categoryList;
    }
}