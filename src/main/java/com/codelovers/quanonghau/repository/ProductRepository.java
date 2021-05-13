package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    Product findByName(String name);

    Product findByIdAndName(Integer id, String name);

    @Query("UPDATE Product p SET p.enabled =?2 WHERE p.id =?1")
    @Modifying
    void updateProductEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.shortDescription LIKE %?1% "
            + "OR p.fullDescription LIKE %?1% "
            + "OR p.category.name LIKE %?1% ")
    Page<Product> findAll(String keyword, Pageable pageable);

    // Select by category id or category parent id without keyword
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
            + "OR p.category.allParentIDs LIKE %?2% ")
    Page<Product> findAllInCategory(Integer categoryId, String categoryMatch, Pageable pageable);

    @Query("SELECT p FROM Product  p WHERE (p.category.id=?1 "
            + "OR p.category.allParentIDs LIKE %?2%) AND "
            + "(p.shortDescription LIKE %?3% "
            + "OR p.fullDescription LIKE %?3% "
            + "OR p.name LIKE %?3% "
            + "OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Integer categoryId, String categoryMatch,
                                   String keyword, Pageable pageable);
}
