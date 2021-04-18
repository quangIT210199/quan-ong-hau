package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.repository.CategoryRepository;
import com.codelovers.quanonghau.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public List<Category> listAll() {
       List<Category> rootCategories =  categoryRepo.findRootCategories();

        return listHierarchicalCategories(rootCategories);
    }


    private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = rootCategory.getChildren();

            for (Category subCatergory : children) {
                String name = "--" + subCatergory.getName();

                hierarchicalCategories.add(Category.copyFull(subCatergory, name));

                listSubHierachicalCategories(hierarchicalCategories, subCatergory, 1);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierachicalCategories(List<Category> hierarchicalCategories, Category parent, int sublevel) {
        Set<Category> children = parent.getChildren();
        int newSublevel = sublevel + 1;

        for (Category subCategory : children) {
            String name ="";

            for (int i=0; i< newSublevel; i++) {
                name += "--";
            }

            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, sublevel);
        }
    }

    //////////////

    @Override
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Category findCategoryById(Integer id) {
        Category category = categoryRepo.findById(id).orElse(null);

        return category;
    }

    // Get Info for Form
    public List<Category> listCategoryUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesDB = categoryRepo.findAll();

        for (Category category : categoriesDB) {
            if(category.getParent() == null) {
                //Get Id and Name
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = category.getChildren();

                for(Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent,
                              int sublevel) {
        int newSublevel = sublevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name ="";
            for (int i = 0 ; i < newSublevel; i++) {
                name += "--";
            }

            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, sublevel);
        }
    }
    // Get Info for Form

}
