package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.models.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EditCategoryDto {
    private Integer id;
    private Category category;
    private List<Category> listCategory = new ArrayList<>();
}
