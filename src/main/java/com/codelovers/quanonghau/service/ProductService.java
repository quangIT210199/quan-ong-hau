package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product findById(Integer id) throws ProductNotFoundException;

    Product findByIdAndName(Integer id, String name);

    List<Product> listAll();

    Product saveProduct(Product product);

    String checkUnique(Integer id, String name);

    void updateProductEnableStatus(Integer id, boolean enabled);

    void deleteProductById(Integer id) throws ProductNotFoundException;

    Product get(Integer id) throws ProductNotFoundException;

    Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId);

    ///////////// FOR USER
    Page<Product> listByCategory(Integer pageNum, Integer categoryId);

    Product findByAlias(String alias) throws ProductNotFoundException;

    Page<Product> search(int pageNum, String keyword);
}
