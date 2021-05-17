package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.exception.BillNotFoundException;
import org.springframework.data.domain.Page;

import java.text.ParseException;
import java.util.List;

public interface BillService {

    Bill createBill(Bill bill) throws ParseException;

    Bill findById(Integer billId);

    void removeBill(Integer bid) throws BillNotFoundException;

    List<Bill> findAllBillByUserId(Integer userId);

    Page<Bill> listByPage(int pageNum, String sortDir);

    void updateBillEnableStatus(Integer id, boolean enabled);
}
