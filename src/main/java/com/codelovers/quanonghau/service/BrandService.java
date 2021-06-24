package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.exception.BrandNotFoudException;
import com.codelovers.quanonghau.models.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {

    List<Brand> listAllForForm();

    Brand createBrand(Brand brand);

    void deleteBrandById(Integer id) throws BrandNotFoudException;

    Brand findById(Integer id) throws BrandNotFoudException;

    String checkUniqueBrand(Integer id, String name);

    Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword);
}
