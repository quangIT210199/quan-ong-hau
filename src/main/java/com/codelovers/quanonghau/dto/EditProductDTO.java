package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.models.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EditProductDTO {
    private Integer numberOfExistingExtraImages;
    private Product product;
    private List<Category> categoryList = new ArrayList<>();
}