package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.service.BillService;
import com.codelovers.quanonghau.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            return new ResponseEntity<>("Không có bill", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

//    @GetMapping(value = "/bill/page", produces = "application/json")
//    public ResponseEntity<?> listBill(@RequestParam(name = "pageNum") Integer pageNum, @RequestParam(name = "sortField") String sortField,
//                                      @RequestParam(name = "sortDir") String sortDir, @RequestParam(name = "keyword") String keyword) {
//
//    }

    // Get info bill ID of user by id
    @GetMapping(value = "/bill", produces = "application/json")
    public ResponseEntity<?> showBillOfUser(@RequestParam(name = "uid") Integer uid) {
        // Làm authen để xác định user
        List<Bill> listBill = billSer.findAllBillByUserId(uid);

        return new ResponseEntity<>(listBill, HttpStatus.OK);
    }

    // Tạo Bill khi click btn CheckOut và gán billId cho các sp trong giỏ hàng
    @PostMapping(value = "/bill/{uid}", produces = "application/json")
    public ResponseEntity<?> createBill(@RequestBody Integer[] cartIds, @PathVariable int uid) {

        if (cartIds.length - 1 < 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Cần check User của giỏ hàng đó thì các CartItem tồn tại BillId chưa
        // Cần làm thêm authen để tìm kiếm cùng user định danh giỏ hàng của user nào
        for (Integer c : cartIds) {
            // Check CartItem có Bill chưa để CreateBill
            CartItem cartItem = cartItemSer.findByIdAndUser(c, uid);
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
        Bill bill = billSer.findById(bid);

        if (bill == null) {
            return new ResponseEntity<>("Không tồn tại", HttpStatus.NO_CONTENT);
        }

        billSer.removeBill(bid);
        return new ResponseEntity<>("DONE", HttpStatus.OK);
    }
}
