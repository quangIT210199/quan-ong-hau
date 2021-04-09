package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.BillRepository;
import com.codelovers.quanonghau.repository.CartItemRepository;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
    public List<Bill> findAllBillByUserId(Integer userId) { // chưa xong
        User u = userRepo.findById(userId).orElse(null);
        if(u == null){
            return null;
        }
        Bill b = null;
        b.getCartItems().get(0).getUser().getId();


        return null;
    }
}
