package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {
//    List<Bill> findBillBy
}
