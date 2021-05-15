package com.codelovers.quanonghau.controller.user;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.user.PagingProductUser;
import com.codelovers.quanonghau.controller.output.user.ViewProductDetail;
import com.codelovers.quanonghau.entity.Category;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.CategoryNotFoundException;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.service.CategoryService;
import com.codelovers.quanonghau.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ProductRestController {

    @Autowired
    private ProductService productSer;

    @Autowired
    private CategoryService categorySer;

    @GetMapping(value = "/c/{category_alias}/", produces = "application/json")
    public ResponseEntity<?> viewCategoryFirstPage(@PathVariable("category_alias") String alias) {
        return viewCategoryByPage(alias, 1);
    }

    @GetMapping(value = "/c/{category_alias}/page/{pageNum}", produces = "application/json")
    public ResponseEntity<?> viewCategoryByPage(@PathVariable("category_alias") String alias,
                                                @PathVariable("pageNum") Integer pageNum) {
        try {
            Category category = categorySer.getCategoryByAlias(alias);
            List<Category> listCategoryParents = categorySer.getCategoryParents(category); // Using for BreandCrumb

            Page<Product> pageProducts = productSer.listByCategory(pageNum, category.getId());
            List<Product> listProducts =  pageProducts.getContent();

            long startCount = (pageNum - 1) * Contrants.PRODUCT_PER_PAGE + 1;
            long endCount =  startCount + Contrants.PRODUCT_PER_PAGE - 1;
            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            PagingProductUser pagingProductUser = new PagingProductUser();
            pagingProductUser.setCategory(category);
            pagingProductUser.setCurrentPage(pageNum);
            pagingProductUser.setTotalPages(pageProducts.getTotalPages());
            pagingProductUser.setStartCount(startCount);
            pagingProductUser.setEndCount(endCount);
            pagingProductUser.setTotalItems(pageProducts.getTotalElements());
            pagingProductUser.setPageTitle(category.getName());
            pagingProductUser.setListCategoryParents(listCategoryParents);
            pagingProductUser.setListProductByCateId(listProducts);

            return new ResponseEntity<>(pagingProductUser, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(value = "/p/product_alias/", produces = "application/json")
    public ResponseEntity<?> viewProductDetail(@PathVariable("product_alias") String alias) {
        try {
            Product product = productSer.findByAlias(alias);
            List<Category> listCategoryParents = categorySer.getCategoryParents(product.getCategory()); // Using for BreandCrumb

            ViewProductDetail viewProductDetail = new ViewProductDetail();
            viewProductDetail.setProduct(product);
            viewProductDetail.setListCategoryParents(listCategoryParents);
            viewProductDetail.setPageTitle(product.getShortName());

            return new ResponseEntity<>(viewProductDetail, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }
}
