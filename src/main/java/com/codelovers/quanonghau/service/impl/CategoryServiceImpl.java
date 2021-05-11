package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;
import com.codelovers.quanonghau.help.PageInfoCategory;
import com.codelovers.quanonghau.repository.CategoryRepository;
import com.codelovers.quanonghau.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public List<Category> listAll() { // This using for Test :v
       List<Category> rootCategories =  categoryRepo.findRootCategories(Sort.by("name").ascending());

        return listHierarchicalCategories(rootCategories, "asc");
    }

    @Override
    public List<Category> listByPage(PageInfoCategory pageInfoCategory,
                                     Integer pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null; // Just find root Category


        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepo.search(keyword, pageable);
        } else {
            pageCategories = categoryRepo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        // Save info of PAGE
        pageInfoCategory.setTotalPages(pageCategories.getTotalPages());
        pageInfoCategory.setTotalElements(pageCategories.getTotalElements());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }

            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));
            // Need Sort children Set
            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCatergory : children) {
                String name = "--" + subCatergory.getName();

                hierarchicalCategories.add(Category.copyFull(subCatergory, name));

                listSubHierachicalCategories(hierarchicalCategories, subCatergory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierachicalCategories(List<Category> hierarchicalCategories, Category parent,
                                              int sublevel, String sortDir) {
        // Need Sort children Set
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSublevel = sublevel + 1;

        for (Category subCategory : children) {
            String name ="";

            for (int i=0; i< newSublevel; i++) {
                name += "--";
            }

            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierachicalCategories(hierarchicalCategories, subCategory, sublevel, sortDir);
        }
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                }
                else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    //////////////
    // Get Info Category for Form
    @Override
    public List<Category> listCategoryUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesDB = categoryRepo.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesDB) {
            if(category.getParent() == null) {
                //Get Id and Name
                categoriesUsedInForm.add(Category.copyIdAndName(category));
                // Need Sort children Set
                Set<Category> children = sortSubCategories(category.getChildren());

                for(Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }

        return categoriesUsedInForm;
    }

    // Find Sub category using Recursive
    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent,
                                             int sublevel) {
        int newSublevel = sublevel + 1;
        // Need sorted children Set
        Set<Category> children = sortSubCategories(parent.getChildren());

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
    @Override
    public Category saveCategory(Category category) {
        Category parent = category.getParent();

        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";

            category.setAllParentIDs(allParentIds);
        }

        return categoryRepo.save(category);
    }

    @Override
    public Category findCategoryById(Integer id) {
        Category category = categoryRepo.findById(id).orElse(null);

        return category;
    }

    @Override
    public String checkUnique(Integer id, String name, String alais) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepo.findByName(name);

        if(isCreatingNew) {
            if (categoryByName != null) {
                return "Duplicate Name";
            }
            else {
                Category categoryByAlais = categoryRepo.findByAlias(alais);
                if(categoryByAlais != null) {
                    return "Duplicate Alias";
                }
            }
        }
        else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "Duplicate Name";
            }
            else {
                Category categoryByAlais = categoryRepo.findByAlias(alais);
                if(categoryByAlais != null && categoryByAlais.getId() != id) {
                    return "Duplicate Alias";
                }
            }
        }

        return "OK";
    }

    @Override
    public void updateCategoryEnableStatus(Integer id, boolean enabled) {
        categoryRepo.updateCategoryEnabledStatus(id, enabled);
    }

    @Override
    public void deleteCategoryById(Integer id) throws CategoryNotFoundException {
        Long count = categoryRepo.countById(id);

        if(count == null || count == 0) {
            throw new CategoryNotFoundException("Counld not found category with ID: " + id);
        }

        categoryRepo.deleteById(id);
    }
}
