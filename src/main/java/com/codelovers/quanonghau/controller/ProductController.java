package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.PagingProduct;
import com.codelovers.quanonghau.dto.EditProductDTO;
import com.codelovers.quanonghau.dto.NewProductDTO;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.ProductImage;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.help.ProductSaveHelper;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.service.ProductService;
import com.codelovers.quanonghau.util.FileUploadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productSer;

    @Autowired
    private CategoryService categorySer;

    @GetMapping(value = "/products", produces = "application/json")
    public ResponseEntity<?> listAllProdut() {

        return new ResponseEntity<>(productSer.listAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/product/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage() {
        return listProduct(1, "name", "ase", null, 0); // 0 is mean ALL Category
    }

    @GetMapping(value = "/product/page", produces = "application/json")
    public ResponseEntity<?> listProduct(@RequestParam(value = "pageNum") Integer pageNum,
                                          @RequestParam(value = "sortField") String sortField,
                                          @RequestParam(value = "sortDir") String sortDir,
                                          @RequestParam(value = "keyword") String keyword,
                                         @RequestParam(value = "categoryID") Integer categoryID) {
        Page<Product> page = productSer.listByPage(pageNum, sortField, sortDir, keyword, categoryID);
        // Using Search with Product => Get all list categories
        List<Category> listCategories = categorySer.listCategoryUsedInForm();

        System.out.println("Category is Selected id: " + categoryID);
        List<Product> listProduct = page.getContent();
        long startCount = (pageNum -1) * Contrants.PRODUCT_PER_PAGE + 1; // Start at index element
        long endCount = startCount + Contrants.PRODUCT_PER_PAGE - 1; // Index of End element

        if (endCount > page.getTotalElements()) { // The last page
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        PagingProduct pagingProduct = new PagingProduct();

        if (categoryID != null) {
            pagingProduct.setCategoryID(categoryID);
        }

        pagingProduct.setProductList(listProduct);
        pagingProduct.setCurrentPage(pageNum);
        pagingProduct.setTotalPages(page.getTotalPages());
        pagingProduct.setStartCount(startCount);
        pagingProduct.setEndCount(endCount);
        pagingProduct.setTotalItems(page.getTotalElements());
        pagingProduct.setSortField(sortField);
        pagingProduct.setSortDir(sortDir);
        pagingProduct.setKeyword(keyword);
        pagingProduct.setReverseSortDir(reverseSortDir);
        pagingProduct.setCategoryList(listCategories);

        return new ResponseEntity<>(pagingProduct, HttpStatus.OK);
    }

    // Need code DTO for return json use for Form Product
    @GetMapping(value = "/product/new", produces = "application/json")
    public ResponseEntity<?> newProduct() {
        List<Category> categoryList = categorySer.listAll();

        Product product = new Product();
        NewProductDTO newProductDTO = new NewProductDTO(product, categoryList);

        return new ResponseEntity<>(newProductDTO, HttpStatus.OK);
    }

    // Cần tách ra làm 2 API
    @PostMapping(value = "/product/save", produces = "application/json")
    public ResponseEntity<?> saveProduct(String productJson,
                                         @RequestParam(value = "fileImage", required = false) MultipartFile mainImage,
                                         @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImage,
                                         @RequestParam(value = "detailIDs", required = false) String [] detailIDs,
                                         @RequestParam(value = "detailNames", required = false) String[] detailNames,
                                         @RequestParam(value = "detailValues", required = false) String[] detailValues,
                                         @RequestParam(value = "imageIDs", required = false) String[] imageIDs,
                                         @RequestParam(value = "imageNames", required = false) String[] imageNames) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        ProductSaveHelper.setMainImageName(mainImage, product);

        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product); // Set image for Extra Image already have in server

        ProductSaveHelper.setNewExtraImageNames(extraImage, product); // Set newExtraImage to the Set collection
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productSer.saveProduct(product);

        ProductSaveHelper.saveUploadImages(mainImage, extraImage, savedProduct);

        ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);

        return new ResponseEntity(savedProduct, HttpStatus.OK);
    }

    @GetMapping(value = "/product/edit" , produces = "application/json")
    public ResponseEntity<?> editProduct(@RequestParam(value = "id") Integer id) {
        try {
            Product product = productSer.get(id);
            List<Category> categoryList = categorySer.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            EditProductDTO editProductDTO = new EditProductDTO();
            editProductDTO.setProduct(product);
            editProductDTO.setCategoryList(categoryList);
            editProductDTO.setNumberOfExistingExtraImages(numberOfExistingExtraImages);

            return new ResponseEntity<>(editProductDTO, HttpStatus.OK);
        } catch (ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/product/{id}/enabled/{status}", produces = "application/json")
    public ResponseEntity<?> updateProductEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean enabled) {
        Product product = productSer.findById(id);
        if(product == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        productSer.updateProductEnableStatus(id, enabled);

        String result = enabled ? "enabled" : "disabled";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/product/delete", produces = "application/json")
    public ResponseEntity<?> deleteProduct(@RequestParam Integer id) {
        try {
            productSer.deleteProductById(id);

            String productExtraImagesDir = "images/product-photo/" + id +"/extras";
            String productImagesDir = "images/product-photo/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/product/check_unique", produces = "application/json")
    public String checkUnique(@Param(value = "id") Integer id, @Param(value = "name") String name) {
        return productSer.checkUnique(id, name);
    }
}
