package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.BillRepository;
import com.codelovers.quanonghau.repository.CartItemRepository;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BillServiceImpl implements BillService {

    @Autowired
    BillRepository billRepo;

    @Autowired
    CartItemRepository cartItemRepo;

    @Autowired
    UserRepository userRepo;

    @Override
    public Bill createBill(Bill bill) { // Khi bấm checkout tạo 1 Bill và add ID vào hết các CartItem k có billID
        Bill b = billRepo.save(bill);

        return b;
    }

    @Override
    public Bill findById(Integer billId) {
        return billRepo.findById(billId).orElse(null);
    }

    @Override
    public void removeBill(Integer bid) {
        Bill bill = billRepo.findById(bid).orElse(null);

        billRepo.delete(bill);
    }

    @Override
    public List<Bill> findAllBillByUserId(Integer userId) { // chưa xong
        User u = userRepo.findById(userId).orElse(null);
        if(u == null){
            return null;
        }
        // Get list billIds
        Set<Integer> listBillId = cartItemRepo.findBillsByUserId(userId);
        if(listBillId == null)
            return null;

        // Get List Bill
        List<Bill> listBill = new ArrayList<>();
        for (Integer id : listBillId) {
            Bill b = billRepo.findById(id).orElse(null);
            listBill.add(b);
        }
        
        return listBill;
    }
}
