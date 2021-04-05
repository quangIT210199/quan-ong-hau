package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.repository.BillRepository;
import com.codelovers.quanonghau.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BillServiceImpl implements BillService {

    @Autowired
    BillRepository billRepo;

    @Override
    public Bill createBill(Bill bill) {
        Bill b = billRepo.save(bill);
        return b;
    }
}
