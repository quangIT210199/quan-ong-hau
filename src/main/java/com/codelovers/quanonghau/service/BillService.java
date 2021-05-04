package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Bill;

import java.util.List;

public interface BillService {

    Bill createBill(Bill bill);

    Bill findById(Integer billId);

    void removeBill(Integer bid);

    List<Bill> findAllBillByUserId(Integer userId);
}
