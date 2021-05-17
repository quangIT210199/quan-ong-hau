package com.codelovers.quanonghau.controller.admin;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.admin.PagingProduct;
import com.codelovers.quanonghau.dto.EditProductDTO;
import com.codelovers.quanonghau.dto.NewProductDTO;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import com.codelovers.quanonghau.export.ProductPdfExporter;
import com.codelovers.quanonghau.help.ProductSaveHelper;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.service.ProductService;
import com.codelovers.quanonghau.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
        long startCount = (pageNum - 1) * Contrants.PRODUCT_PER_PAGE + 1; // Start at index element
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
        product.setEnabled(true);
        product.setInStock(true);

        NewProductDTO newProductDTO = new NewProductDTO(product, categoryList);

        return new ResponseEntity<>(newProductDTO, HttpStatus.OK);
    }

    // Cần tách ra làm 2 API
    @PostMapping(value = "/product/save", produces = "application/json")
    public ResponseEntity<?> saveProduct(String productJson,
                                         @RequestParam(value = "fileImage", required = false) MultipartFile mainImage,
                                         @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImage,
                                         @RequestParam(value = "detailIDs", required = false) String[] detailIDs,
                                         @RequestParam(value = "detailNames", required = false) String[] detailNames,
                                         @RequestParam(value = "detailValues", required = false) String[] detailValues,
                                         @RequestParam(value = "imageIDs", required = false) String[] imageIDs,
                                         @RequestParam(value = "imageNames", required = false) String[] imageNames,
                                         @RequestParam(value = "heightQR", defaultValue = "200") Integer heightQR,
                                         @RequestParam(value = "widthQR", defaultValue = "200") Integer widthQR) throws IOException, WriterException {
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        ProductSaveHelper.setMainImageName(mainImage, product);

        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product); // Set image for Extra Image already have in server

        ProductSaveHelper.setNewExtraImageNames(extraImage, product); // Set newExtraImage to the Set collection
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        product.setQrCodeImage("QRCode.png");

        Product savedProduct = productSer.saveProduct(product);
        System.out.println(savedProduct.getQrCodeImage());
        // Save QR code
        ProductSaveHelper.saveUploadQRCode(savedProduct, heightQR, widthQR);

        ProductSaveHelper.saveUploadImages(mainImage, extraImage, savedProduct);

        ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);

        return new ResponseEntity(savedProduct, HttpStatus.OK);
    }

    // Get Infomation Product when scanner QRCode image
    @GetMapping(value = "/product/{id}/{name}")
    public ResponseEntity<?> getProductByQRCode(@PathVariable(name = "id") Integer id,
                                               @PathVariable(name = "name") String name) {
        Product product = productSer.findByIdAndName(id, name);
        if (product == null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(product, HttpStatus.OK);
    }

    // Download QR code in PDF

    @GetMapping(value = "/product/edit", produces = "application/json")
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
        try {
            Product product = productSer.findById(id);

            productSer.updateProductEnableStatus(id, enabled);

            String result = enabled ? "enabled" : "disabled";

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/product/delete", produces = "application/json")
    public ResponseEntity<?> deleteProduct(@RequestParam Integer id) {
        try {
            productSer.deleteProductById(id);

            String productExtraImagesDir = "images/product-photo/" + id + "/extras/";
            String productImagesDir = "images/product-photo/" + id;
            String qrCodeImageDir = "images/product-photo/" + id + "/qrcode/";

            FileUploadUtil.removeDir(qrCodeImageDir);
            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            return new ResponseEntity<>("Delete success", HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/product/check_unique", produces = "application/json")
    public ResponseEntity<?> checkUnique(@RequestParam(value = "id") Integer id, @RequestParam(value = "name") String name) {
        return new ResponseEntity<>(productSer.checkUnique(id, name), HttpStatus.OK);
    }

    /*PDF*/
    @GetMapping("/product/exportQR/pdf/{id}")
    public ResponseEntity<?> exportQRCodeToPDF(HttpServletResponse response, @PathVariable(name = "id") Integer id) throws IOException {
        try {
            Product product = productSer.findById(id);

            ProductPdfExporter exporter = new ProductPdfExporter();
            exporter.exportQRCode(product, response);

            return new ResponseEntity<>("Done", HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}
