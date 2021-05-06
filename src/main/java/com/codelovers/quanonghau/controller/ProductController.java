package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productSer;

    @GetMapping(value = "/product", produces = "application/json")
    public ResponseEntity<?> getProduct() {

        return null;
    }

}
