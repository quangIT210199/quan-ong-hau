package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Bill;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface BillRepository extends PagingAndSortingRepository<Bill, Integer> {

    Long countById(Integer id);

    @Query("UPDATE Bill b SET b.enabled = ?2 WHERE b.id =?1")
    @Modifying
    void updateBillEnabledStatus(Integer id, boolean enabled);
}
