package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.service.BillService;
import com.codelovers.quanonghau.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bills")
public class BillRestController {

    @Autowired
    BillService billSer;

    @Autowired
    CartItemService cartItemSer;

    @GetMapping(value = "/bill/{cid}")
    public ResponseEntity<?> createBill(){
        //need authen in here



        return null;
    }
}
