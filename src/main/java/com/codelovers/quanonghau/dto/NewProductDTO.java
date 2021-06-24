package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.models.Brand;
import com.codelovers.quanonghau.models.Category;
import com.codelovers.quanonghau.models.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewProductDTO {
    private Product product;
    private List<Brand> brandList = new ArrayList<>();
}
