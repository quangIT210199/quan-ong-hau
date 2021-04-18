package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.dto.CategoryDTO;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.util.FileUploadUtil;
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
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    CategoryService categorySer;

    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<?> listAll(){
        List<Category> listCate = categorySer.listAll();

        return new ResponseEntity<>(listCate, HttpStatus.OK);
    }

    // This API using when open form to create or update
    @GetMapping(value = "/categories/new", produces = "application/json")
    public ResponseEntity<?> newCategory(){
        List<Category> listCate = categorySer.listCategoryUsedInForm();

        return new ResponseEntity<>(listCate, HttpStatus.OK);
    }

    @PostMapping(value = "/categories/save",consumes = "multipart/form-data",produces = "application/json")
    public ResponseEntity<?> saveCategory(String categoryJson, @RequestParam("imageFile")MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Category category = mapper.readValue(categoryJson, Category.class);

        System.out.println(category.getName());

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

    @GetMapping(value = "/categories/edit", produces = "application/json")
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
}
