package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product findById(Integer id);

    List<Product> listAll();

    Product saveProduct(Product product);

    String checkUnique(Integer id, String name);

    void updateProductEnableStatus(Integer id, boolean enabled);

    void deleteProductById(Integer id) throws ProductNotFoundException;

    Product get(Integer id) throws ProductNotFoundException;

    Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId);
}
