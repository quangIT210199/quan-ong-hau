package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.dto.EditProductDTO;
import com.codelovers.quanonghau.dto.NewProductDTO;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.service.ProductService;
import com.codelovers.quanonghau.util.FileUploadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productSer;

    @Autowired
    private CategoryService categorySer;

    @GetMapping(value = "/products", produces = "application/json")
    public ResponseEntity<?> getAllProdut() {

        return new ResponseEntity<>(productSer.listAll(), HttpStatus.OK);
    }

    // Need code DTO for return json use for Form Product
    @GetMapping(value = "/product/new", produces = "application/json")
    public ResponseEntity<?> newProduct() {
        List<Category> categoryList = categorySer.listAll();

        Product product = new Product();
        NewProductDTO newProductDTO = new NewProductDTO(product, categoryList);

        return new ResponseEntity<>(newProductDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/product/save", produces = "application/json")
    public ResponseEntity<?> saveProduct(String productJson,
                                         @RequestParam(value = "fileImage", required = false) MultipartFile mainImage,
                                         @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImage,
                                         @RequestParam(value = "detailNames", required = false) String[] detailNames,
                                         @RequestParam(value = "detailValues", required = false) String[] detailValues,
                                         @RequestParam(value = "imageIDs", required = false) Integer[] imageIDs,
                                         @RequestParam(value = "imageNames", required = false) String[] imageNames) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        setExtraImageNames(extraImage, product);

        setExistingExtraImageNames(imageIDs, imageNames, product); // Set image for Extra Image already have in server

        setMainImageName(mainImage, product);
        setProductDetails(detailNames, detailValues, product);

        Product savedProduct = productSer.saveProduct(product);

        saveUploadImages(mainImage, extraImage, savedProduct);

        return new ResponseEntity(product, HttpStatus.OK);
    }

    private void setExistingExtraImageNames(Integer[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        for (int count = 0; count < imageIDs.length; count++) {
//            String
        }
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];

            if(!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }
    }

    private void saveUploadImages(MultipartFile mainImage, MultipartFile[] extraImage, Product savedProduct) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "images/product-photo/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (extraImage.length > 0) {
            String uploadDir = "images/product-photo/" + savedProduct.getId() +"/extras/";

            for (MultipartFile multipartFile : extraImage) {
                if (mainImage.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    private void setExtraImageNames(MultipartFile[] extraImageFilles, Product product) {
        if(extraImageFilles.length > 0) {
            for (MultipartFile multipartFile : extraImageFilles) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageFile, Product product) {
        if (!mainImageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageFile.getOriginalFilename());
            product.setMainImage(fileName);
        }
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
