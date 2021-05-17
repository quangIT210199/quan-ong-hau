package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.exception.BillNotFoundException;
import com.codelovers.quanonghau.repository.BillRepository;
import com.codelovers.quanonghau.repository.CartItemRepository;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public Bill createBill(Bill bill) throws ParseException { // Khi bấm checkout tạo 1 Bill và add ID vào hết các CartItem k có billID
        bill.setCreateTime(new Date());
        bill.setEnabled(true);

        return billRepo.save(bill);
    }

    @Override
    public Bill findById(Integer billId) {
        return billRepo.findById(billId).orElse(null);
    }

    @Override
    public void removeBill(Integer bid) throws BillNotFoundException {
        Long count = billRepo.countById(bid);

        if (count == 0 || count == null) {
            throw new BillNotFoundException("Could not found bill with id: " + bid);
        }

        billRepo.deleteById(bid);
    }

    @Override
    public List<Bill> findAllBillByUserId(Integer userId) { // chưa xong
        User u = userRepo.findById(userId).orElse(null);
        if (u == null) {
            return null;
        }
        // Get list billIds
        Set<Integer> listBillId = cartItemRepo.findBillsByUserId(userId);
        if (listBillId == null)
            return null;

        // Get List Bill
        List<Bill> listBill = new ArrayList<>();
        for (Integer id : listBillId) {
            Bill b = billRepo.findById(id).orElse(null);
            listBill.add(b);
        }

        return listBill;
    }

    @Override
    public Page<Bill> listByPage(int pageNum, String sortDir) {
        Sort sort = null;
        sort = sortDir.equals("acs") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.BILL_PER_PAGE, sort);

        return billRepo.findAll(pageable);
    }

    @Override
    public void updateBillEnableStatus(Integer id, boolean enabled) {
        billRepo.updateBillEnabledStatus(id, enabled);
    }
}
