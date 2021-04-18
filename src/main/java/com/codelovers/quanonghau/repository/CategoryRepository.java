package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ")
    List<Category> findRootCategories();

    Category findByName(String name);

    Category findByAlias(String alias);
}
