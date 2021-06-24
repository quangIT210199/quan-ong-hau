package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.exception.BrandNotFoudException;
import com.codelovers.quanonghau.models.Brand;
import com.codelovers.quanonghau.repository.BrandRepository;
import com.codelovers.quanonghau.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepo;

    @Override
    public List<Brand> listAllForForm() {
        return brandRepo.findAll();
    } // search brand for Edit Product or New in Form

    @Override
    public Brand createBrand(Brand brand) {
        return brandRepo.save(brand);
    }

    @Override
    public void deleteBrandById(Integer id) throws BrandNotFoudException {
        Long countById = brandRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoudException("Counld not found brand with id: " + id);
        }

        brandRepo.deleteById(id);
    }

    @Override
    public Brand findById(Integer id) throws BrandNotFoudException {
        try {
            return brandRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new BrandNotFoudException("Counld not found brand with id: " + id);
        }
    }

    @Override
    public String checkUniqueBrand(Integer id, String name) {
        Boolean isCreatingNew = (id == 0 || id == null);

        Brand brand = brandRepo.findByName(name);

        if (isCreatingNew) {
            if (brand != null) return "Duplicate";
        } else {
            if (brand != null && brand.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    @Override
    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.BRAND_PER_PAGE, sort);
        if (keyword != null) {
            return brandRepo.findAll(keyword, pageable);
        }

        return brandRepo.findAll(pageable);
    }
}
