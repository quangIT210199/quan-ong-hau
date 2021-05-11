package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;
import com.codelovers.quanonghau.help.PageInfoCategory;

import java.util.List;

public interface CategoryService {

    List<Category> listAll();

    List<Category> listCategoryUsedInForm(); // Get list all Category using in form

//    List<Category> listHierarchicalCategories();

    List<Category> listByPage(PageInfoCategory pageInfoCategory, Integer pageNum, String sortDir, String keyword);

    Category saveCategory(Category category);

    Category findCategoryById(Integer id);

    String checkUnique(Integer id, String name, String alais);

    void updateCategoryEnableStatus(Integer id, boolean enabled);

    void deleteCategoryById(Integer id) throws CategoryNotFoundException;
}
