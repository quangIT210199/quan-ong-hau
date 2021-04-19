package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;

import java.util.List;

public interface CategoryService {

    List<Category> listAll();

    List<Category> listCategoryUsedInForm();

//    List<Category> listHierarchicalCategories();

    Category saveCategory(Category category);

    Category findCategoryById(Integer id);

    String checkUnique(Integer id, String name, String alais);

    void updateCategoryEnableStatus(Integer id, boolean enabled);

    void deleteCategoryById(Integer id) throws CategoryNotFoundException;
}
