package com.codelovers.quanonghau.controller.output.user;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ViewProductDetail {
    private Product product;
    private List<Category> listCategoryParents = new ArrayList<>();
    private String pageTitle;
}
