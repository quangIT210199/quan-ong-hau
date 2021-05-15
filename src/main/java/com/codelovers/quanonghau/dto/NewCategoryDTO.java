package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewCategoryDTO {
    private Category category;
    private List<Category> listCategories = new ArrayList<>();
}
