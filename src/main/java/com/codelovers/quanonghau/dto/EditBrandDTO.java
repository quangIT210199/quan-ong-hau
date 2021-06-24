package com.codelovers.quanonghau.dto;

import com.codelovers.quanonghau.models.Brand;
import com.codelovers.quanonghau.models.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EditBrandDTO {
    private Brand brand;
    private List<Category> listCate = new ArrayList<>();
}
