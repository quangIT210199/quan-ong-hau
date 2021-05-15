package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EditCategoryDTO {
    private Integer id;
    private Category category;
    private List<Category> listCategory = new ArrayList<>();
}
