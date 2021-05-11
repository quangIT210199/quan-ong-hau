package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.PagingCategories;
import com.codelovers.quanonghau.dto.CategoryDTO;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;
import com.codelovers.quanonghau.help.PageInfoCategory;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categorySer;

    // For test
    @GetMapping(value = "/category/{id}", produces = "application/json")
    public ResponseEntity<?> getCateById(@PathVariable(name = "id") Integer id) {

        return new ResponseEntity<>(categorySer.findCategoryById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/category", produces = "application/json")
    public ResponseEntity<?> listAll(){
        List<Category> listCate = categorySer.listAll();

        return new ResponseEntity<>(listCate, HttpStatus.OK);
    }
    // For Test

    @GetMapping(value = "/category/categories/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage(@RequestParam(value = "sortDir") String sortDir) {
        return listByPage(1, sortDir, null);
    }

    @GetMapping(value = "/category/categories/page", produces = "application/json")
    public ResponseEntity<?> listByPage(@RequestParam(value = "pageNum") Integer pageNum,
                                        @RequestParam(value = "sortDir") String sortDir,
                                        @RequestParam(value = "keyword") String keyword) { //keyword search by name Cate
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        PageInfoCategory pageInfoCategory = new PageInfoCategory(); // For save info of page
        List<Category> listCategories = categorySer.listByPage(pageInfoCategory, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * Contrants.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + Contrants.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfoCategory.getTotalElements()) {
            endCount = pageInfoCategory.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        PagingCategories pagingCategories = new PagingCategories();

        pagingCategories.setTotalPages(pageInfoCategory.getTotalPages());
        pagingCategories.setTotalItems(pageInfoCategory.getTotalElements());
        pagingCategories.setCurrentPage(pageNum);
        pagingCategories.setReverseSortDir(reverseSortDir);
        pagingCategories.setSortField("name");
        pagingCategories.setSortDir(sortDir);
        pagingCategories.setStartCount(startCount);
        pagingCategories.setEndCount(endCount);
        pagingCategories.setKeyword(keyword);

        pagingCategories.setCategoryList(listCategories);

        return new ResponseEntity<>(pagingCategories, HttpStatus.OK);
    }

    // This API using when open form to create or update
    @GetMapping(value = "/category/new", produces = "application/json")
    public ResponseEntity<?> newCategory(){
        List<Category> listCate = categorySer.listCategoryUsedInForm();

        return new ResponseEntity<>(listCate, HttpStatus.OK);
    }

    // Cần tách ra làm 2 API
    @PostMapping(value = "/category/save",consumes = "multipart/form-data",produces = "application/json")
    public ResponseEntity<?> saveCategory(String categoryJson, @RequestParam("imageFile")MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Category category = mapper.readValue(categoryJson, Category.class);

        if(!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categorySer.saveCategory(category);
            String uploadDir = "images/category-photo/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }
        else {
            categorySer.saveCategory(category);
        }

        return new ResponseEntity<>(category,HttpStatus.OK);
    }

    @GetMapping(value = "/category/edit", produces = "application/json")
    public ResponseEntity<CategoryDTO> editCategory(@RequestParam(value = "id") Integer id) {
        Category category = categorySer.findCategoryById(id);

        if(category == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        List<Category> listCategory = categorySer.listCategoryUsedInForm();

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setCategory(category);
        categoryDTO.setListCategory(listCategory);

        return new ResponseEntity<CategoryDTO>(categoryDTO,HttpStatus.OK);
    }

    @PostMapping(value = "/category/check_unique", produces = "application/json")
    public ResponseEntity<?> checkUniqueCategories(@Param(value = "id") Integer id, @Param(value = "name") String name,
                                                   @Param(value = "alias") String alias){
        String result = categorySer.checkUnique(id, name, alias);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/category/{id}/enabled/{status}", produces = "application/json")
    public ResponseEntity<?> updateCategoryEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled) {
        Category category = categorySer.findCategoryById(id);
        if(category == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        categorySer.updateCategoryEnableStatus(id, enabled);
        String result = enabled ? "enabled" : "disabled";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/category/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "id") Integer id) throws CategoryNotFoundException {
        try {
            categorySer.deleteCategoryById(id);
            String categoryDir = "images/category-photo/" + id;

            FileUploadUtil.removeDir(categoryDir);
            return new ResponseEntity<>("Delete done" + id,HttpStatus.NO_CONTENT);
        } catch (CategoryNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}
