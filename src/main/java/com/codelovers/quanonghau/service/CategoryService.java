package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listAll();

    List<Category> listCategoryUsedInForm();

//    List<Category> listHierarchicalCategories();

    Category saveCategory(Category category);

    Category findCategoryById(Integer id);
}
