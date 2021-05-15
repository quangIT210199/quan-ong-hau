package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.repository.ProductRepository;
import com.codelovers.quanonghau.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepo;

    @Override
    public Product findById(Integer id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public Product findByIdAndName(Integer id, String name) {
        return productRepo.findByIdAndName(id, name);
    }

    @Override
    public List<Product> listAll() {
        return (List<Product>) productRepo.findAll();
    }

    @Override
    public Page<Product> listByPage(int pageNum, String sortField, String sortDir,
                                    String keyword, Integer categoryId) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.PRODUCT_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) { // Search by filter, need check empty
            if (categoryId != null && categoryId > 0) {
                String categoryMatch = "-" + categoryId + "-";
                return productRepo.searchInCategory(categoryId, categoryMatch, keyword, pageable);
            }
            return productRepo.findAll(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryMatch = "-" + categoryId + "-";
            return productRepo.findAllInCategory(categoryId, categoryMatch, pageable);
        }

        return productRepo.findAll(pageable);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepo.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";
    }

    @Override
    public void updateProductEnableStatus(Integer id, boolean enabled) {
        productRepo.updateProductEnabledStatus(id, enabled);
    }

    @Override
    public void deleteProductById(Integer id) throws ProductNotFoundException {
        Long count = productRepo.countById(id);

        if (count == 0 || count == null) {
            throw new ProductNotFoundException("Counld not found product with id: " + id);
        }

        productRepo.deleteById(id);
    }

    @Override
    public Product get(Integer id) throws ProductNotFoundException {
        try {
            return productRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ProductNotFoundException("Counld not find product with id: " + id);
        }
    }

    /////////////////// FOR USER
    @Override
    public Page<Product> listByCategory(Integer pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId)+ "-";
        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.PRODUCT_PER_PAGE);

        return productRepo.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    @Override
    public Product findByAlias(String alias) throws ProductNotFoundException {
        Product product = productRepo.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could find product with alias: " + alias);
        }

        return product;
    }
}
