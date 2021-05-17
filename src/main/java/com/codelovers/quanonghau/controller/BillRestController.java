package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.admin.PagingBill;
import com.codelovers.quanonghau.models.Bill;
import com.codelovers.quanonghau.models.CartItem;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.exception.BillNotFoundException;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.service.BillService;
import com.codelovers.quanonghau.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillRestController {

    @Autowired
    BillService billSer;

    @Autowired
    CartItemService cartItemSer;

    @GetMapping(value = "/bill/{bid}", produces = "application/json")
    public ResponseEntity<?> getBill(@PathVariable(name = "bid") Integer bid) {

        Bill bill = billSer.findById(bid);
        if (bill == null) {
            return new ResponseEntity<>("Not found bill", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @GetMapping(value = "/bill/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage(@RequestParam(name = "pageNum") Integer pageNum,
                                           @RequestParam(name = "sortDir") String sortDir) {
        return listBill(1, "asc");
    }

    @GetMapping(value = "/bill/page", produces = "application/json")
    public ResponseEntity<?> listBill(@RequestParam(name = "pageNum") Integer pageNum,
                                      @RequestParam(name = "sortDir") String sortDir) {
        Page<Bill> billPage = billSer.listByPage(pageNum, sortDir);

        List<Bill> listBill = billPage.getContent();
        long startCount = (pageNum - 1) * Contrants.BILL_PER_PAGE + 1;
        long endCount = startCount + Contrants.BILL_PER_PAGE - 1;

        if (endCount > billPage.getTotalElements()) {
            endCount = billPage.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "asc" : "desc";

        PagingBill pagingBill = new PagingBill();
        pagingBill.setCurrentPage(pageNum);
        pagingBill.setTotalPages(billPage.getTotalPages());
        pagingBill.setTotalItems(billPage.getTotalElements());
        pagingBill.setStartCount(startCount);
        pagingBill.setEndCount(endCount);
        pagingBill.setSortDir(sortDir);
        pagingBill.setReverseSortDir(reverseSortDir);
        pagingBill.setBillList(listBill);

        return new ResponseEntity<>(pagingBill, HttpStatus.OK);
    }

    // Get info bill ID of user by id
    @GetMapping(value = "/bill", produces = "application/json")
    public ResponseEntity<?> showBillOfUser(@AuthenticationPrincipal CustomUserDetails loggedUser) {
        User user = loggedUser.getUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Làm authen để xác định user
        List<Bill> listBill = billSer.findAllBillByUserId(user.getId());

        return new ResponseEntity<>(listBill, HttpStatus.OK);
    }

    // Tạo Bill khi click btn CheckOut và gán billId cho các sp trong giỏ hàng
    @PostMapping(value = "/bill", produces = "application/json")
    public ResponseEntity<?> createBill(@RequestBody Integer[] cartIds,
                                        @AuthenticationPrincipal CustomUserDetails loggedUser) throws ParseException {
        User user = loggedUser.getUser();

        if (cartIds.length - 1 < 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Cần check User của giỏ hàng đó thì các CartItem tồn tại BillId chưa
        // Cần làm thêm authen để tìm kiếm cùng user định danh giỏ hàng của user nào
        for (Integer c : cartIds) {
            // Check CartItem có Bill chưa để CreateBill
            CartItem cartItem = cartItemSer.findByIdAndUser(c, user.getId());
            if (cartItem.getBill() != null) {
                return new ResponseEntity<>("Khong hợp lệ", HttpStatus.NO_CONTENT);
            }
        }

        Bill b = new Bill();
        // Tạo bill vào set cho các CartItem BillId
        Bill bill = billSer.createBill(b);

        Integer billId = bill.getId();
        System.out.println("ID của Bill: " + billId);
        //Xét billId vào CardItem
        cartItemSer.updateBillId(billId, cartIds);

        return new ResponseEntity<>("Tạo Bill thành công!", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(value = "/bill/remove/{bid}", produces = "application/json")
    public ResponseEntity<?> removeBill(@PathVariable(name = "bid") Integer bid) {
        try {
            billSer.removeBill(bid);

            return new ResponseEntity<>("Delete success", HttpStatus.OK);
        } catch (BillNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/bill/{id}/status/{enabled}", produces = "application/json")
        public ResponseEntity<?> updateBillEnabledStatus(@PathVariable(name = "id") Integer id,
                                                         @PathVariable(name = "enabled") boolean enabled) {
        billSer.updateBillEnableStatus(id, enabled);

        String result = enabled ? "enabled" : "disabled";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
