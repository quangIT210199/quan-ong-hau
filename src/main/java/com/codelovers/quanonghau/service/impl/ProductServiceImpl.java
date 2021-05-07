package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.repository.ProductRepository;
import com.codelovers.quanonghau.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
    public List<Product> listAll() {
        return (List<Product>) productRepo.findAll();
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
        Long count = productRepo.count();

        if(count == 0 || count == null) {
            throw new ProductNotFoundException("Counld not found product with id: " + id);
        }

        productRepo.deleteById(id);
    }
}
