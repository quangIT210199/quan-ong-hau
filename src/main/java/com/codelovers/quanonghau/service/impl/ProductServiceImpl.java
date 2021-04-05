package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.repository.ProductRepository;
import com.codelovers.quanonghau.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepo;

    @Override
    public Product findById(Integer id) {
        return productRepo.findById(id).orElse(null);
    }
}
