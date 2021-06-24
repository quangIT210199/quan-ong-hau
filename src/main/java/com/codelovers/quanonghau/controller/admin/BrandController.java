package com.codelovers.quanonghau.controller.admin;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.admin.PagingBrand;
import com.codelovers.quanonghau.dto.CategoryDTO;
import com.codelovers.quanonghau.dto.EditBrandDTO;
import com.codelovers.quanonghau.dto.NewBrandDTO;
import com.codelovers.quanonghau.exception.BrandNotFoudException;
import com.codelovers.quanonghau.models.Brand;
import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.service.BrandService;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.utils.FileUploadUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// This Controlle using for manager Brand
@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @Autowired
    private BrandService brandSer;

    @Autowired
    private CategoryService categorySer;

    @GetMapping(value = "/brands", produces = "application/json")
    public ResponseEntity<?> listAll() {
        List<Brand> listAll = brandSer.listAllForForm();
        return new ResponseEntity<>(listAll, HttpStatus.OK);
    }

    @GetMapping(value = "/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage() {
        return listByPage(1, "asc","name", null);
    }

    @GetMapping(value = "/page", produces = "application/json")
    public ResponseEntity<?> listByPage(@RequestParam(name = "pageNum") Integer pageNum, @RequestParam(name = "sortDir") String sortDir,
                                        @RequestParam(name = "sortField") String sortField, @RequestParam(name = "keyword") String keyword) {
        Page<Brand> page = brandSer.listByPage(pageNum, sortField, sortDir, keyword);

        List<Brand> listResult = page.getContent();

        long startCount = (pageNum - 1) * Contrants.BRAND_PER_PAGE + 1;
        long endCount = startCount + Contrants.BRAND_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        PagingBrand pagingBrand = new PagingBrand();
        pagingBrand.setStartCount(startCount);
        pagingBrand.setEndCount(endCount);
        pagingBrand.setCurrentPage(pageNum);
        pagingBrand.setTotalItem(page.getTotalElements());
        pagingBrand.setTotalPage(page.getTotalPages());
        pagingBrand.setSortDir(sortDir);
        pagingBrand.setSortField(sortField);
        pagingBrand.setKeyword(keyword);
        pagingBrand.setReverseSortDir(reverseSortDir);
        pagingBrand.setListBrand(listResult);

        return new ResponseEntity<>(pagingBrand, HttpStatus.OK);
    }

    @GetMapping(value = "/brands/{id}/categories")
    public ResponseEntity<?> listCategoriesByBrand(@RequestParam(name = "id") Integer brandId) { // Using when choose Brand in Form
        List<CategoryDTO> categoryListOfBrand = new ArrayList<>();

        try {
            Brand brand = brandSer.findById(brandId);
            Set<Category> categorySet = brand.getCategories();

            for (Category c : categorySet) {
                CategoryDTO categoryDTO = new CategoryDTO(c.getId(), c.getName());
                categoryListOfBrand.add(categoryDTO);
            }

            return new ResponseEntity<>(categoryListOfBrand, HttpStatus.OK);
        } catch (BrandNotFoudException e) {
            e.printStackTrace();

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/save",consumes = {"multipart/form-data","application/json"} ,produces = "application/json")
    public ResponseEntity<?> saveBrand(String brandJson, @RequestParam(name = "logoFile") MultipartFile file) throws IOException {
        Gson gson = new Gson();
        Brand brand = gson.fromJson(brandJson, Brand.class);

        Brand savedBrand = null;
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            brand.setLogo(fileName);
            savedBrand = brandSer.createBrand(brand);

            String uploadDir = "images/brand-photo" + "/" + brand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            if (brand.getLogo().isEmpty()) brand.setLogo(null);
            savedBrand = brandSer.createBrand(brand);
        }

        return new ResponseEntity<>(savedBrand, HttpStatus.OK);
    }

    @GetMapping(value = "/delete", produces = "application/json")
    public ResponseEntity<?> removeBrand(@RequestParam(name = "id") Integer id) {
        try {
            brandSer.deleteBrandById(id);

            String brandDir = "images/brand-photo/" + id;
            FileUploadUtil.removeDir(brandDir);

            return new ResponseEntity<>("Delete Succes", HttpStatus.OK);
        } catch (BrandNotFoudException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/new", produces = "application/json")
    public ResponseEntity<?> newBrand() {
        Brand brand = new Brand();
        List<Category> listCategory = categorySer.listCategoryUsedInForm();

        NewBrandDTO newBrandDto = new NewBrandDTO();
        newBrandDto.setBrand(brand);
        newBrandDto.setCategoryList(listCategory);
        return new ResponseEntity<>(newBrandDto, HttpStatus.OK);
    }

    @GetMapping(value = "/edit", produces = "application/json")
    public ResponseEntity<?> editBrand(@RequestParam(name = "id") Integer id) {
        try {
            Brand brandEdit = brandSer.findById(id);

            List<Category> listCate = categorySer.listCategoryUsedInForm();

            EditBrandDTO editBrandDto = new EditBrandDTO();
            editBrandDto.setBrand(brandEdit);
            editBrandDto.setListCate(listCate);

            return new ResponseEntity<>(editBrandDto, HttpStatus.OK);
        } catch (BrandNotFoudException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/check_unique", produces = "application/json")
    public ResponseEntity<?> checkUnique(@RequestParam(name = "id") Integer id, @RequestParam(name = "name") String name) {
        return new ResponseEntity<>(brandSer.checkUniqueBrand(id, name), HttpStatus.OK);
    }
}
