package com.codelovers.quanonghau.controller.admin;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.admin.PagingCategories;
import com.codelovers.quanonghau.dto.EditCategoryDTO;
import com.codelovers.quanonghau.dto.NewCategoryDTO;
import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;
import com.codelovers.quanonghau.help.PageInfoCategory;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.utils.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> listAll() {
        List<Category> listCate = categorySer.listCategoryUsedInForm();

        return new ResponseEntity<>(listCate, HttpStatus.OK);
    }
    // For Test

    @GetMapping(value = "/category/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage(@RequestParam(value = "sortDir") String sortDir) {
        return listByPage(1, sortDir, null);
    }

    @GetMapping(value = "/category/page", produces = "application/json")
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
    public ResponseEntity<?> newCategory() {
        Category category = new Category();
        List<Category> listCate = categorySer.listCategoryUsedInForm();

        NewCategoryDTO newCategoryDTO = new NewCategoryDTO();
        newCategoryDTO.setCategory(category);
        newCategoryDTO.setListCategories(listCate);

        return new ResponseEntity<>(newCategoryDTO, HttpStatus.OK);
    }

    // C???n t??ch ra l??m 2 API
    @PostMapping(value = "/category/save", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> saveCategory(String categoryJson, @RequestParam("imageFile") MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Category category = mapper.readValue(categoryJson, Category.class);

        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categorySer.saveCategory(category);
            String uploadDir = "images/category-photo/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            categorySer.saveCategory(category);
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping(value = "/category/edit", produces = "application/json")
    public ResponseEntity<EditCategoryDTO> editCategory(@RequestParam(name = "id") Integer id) {
        Category category = categorySer.findCategoryById(id);

        if (category == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        List<Category> listCategory = categorySer.listCategoryUsedInForm();

        EditCategoryDTO editCategoryDTO = new EditCategoryDTO();
        editCategoryDTO.setId(category.getId());
        editCategoryDTO.setCategory(category);
        editCategoryDTO.setListCategory(listCategory);

        return new ResponseEntity<EditCategoryDTO>(editCategoryDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/category/check_unique", produces = "application/json")
    public ResponseEntity<?> checkUniqueCategories(@RequestParam(value = "id") Integer id, @RequestParam(value = "name") String name,
                                                   @RequestParam(value = "alias") String alias) {
        String result = categorySer.checkUnique(id, name, alias);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/category/{id}/enabled/{status}", produces = "application/json")
    public ResponseEntity<?> updateCategoryEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled) {
        Category category = categorySer.findCategoryById(id);
        if (category == null) {
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
            return new ResponseEntity<>("Delete done" + id, HttpStatus.NO_CONTENT);
        } catch (CategoryNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
