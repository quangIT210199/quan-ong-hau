package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.models.CartItem;
import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.exception.ProductNotFoundException;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.service.CartItemService;
import com.codelovers.quanonghau.service.ProductService;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class ShoppingCartRestController {

    @Autowired
    ProductService productSer;

    @Autowired
    CartItemService cartItemSer;

    @Autowired
    UserService userSer;

    // Show shopping Cart
//    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping(value = "/cart", produces = "application/json")
    public ResponseEntity<?> showShoppingCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Need to code authen for get user instance
        //User user = userSer.getCurrentlyLoggedInUser(authentication);
        // And here need use uid
        User user = customUserDetails.getUser();

        List<CartItem> cartItems = cartItemSer.listCartItems(user);
        for (CartItem cartItem : cartItems) {
            System.out.println(cartItem.getProduct().getName());
        }
        if (cartItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen
    @GetMapping(value = "/cart/add", produces = "application/json")
    public ResponseEntity<?> addProductToCart(@RequestParam(name = "pid") Integer pid,
                                              @RequestParam(name = "qty") Integer qty, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
//            System.out.println("Null?");
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
        if (customUserDetails == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            User user = customUserDetails.getUser();

            Product product = productSer.findById(pid);

            Integer addedQuantity = cartItemSer.addProduct(pid, qty, user);
            System.out.println("Th??nh c??ng");
            return new ResponseEntity<>(addedQuantity, HttpStatus.OK);
        } catch (ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}/{qty}
    // Calculator when click button
    @PostMapping(value = "/cart/update", produces = "application/json")
    public ResponseEntity<?> updateQuantity(@RequestParam(name = "pid") Integer pid,
                                            @RequestParam(name = "qty") Integer qty, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // Can check billID co ko. Co thi k dc tang quantity
        if (customUserDetails == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = customUserDetails.getUser();

        try {
            Product product = productSer.findById(pid);
        } catch (ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }

        CartItem c = cartItemSer.exitBillId(pid, user);
        if (c == null) { // Product is in Bill
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        float subTotal = cartItemSer.updateQuantity(qty, pid, user);
        System.out.println("SubTotal = " + subTotal);
        return new ResponseEntity<>(qty, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}
    @GetMapping(value = "/cart/remove", produces = "application/json")
    public ResponseEntity<?> removeProductFromCart(@RequestParam(name = "pid") Integer pid, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            User user = customUserDetails.getUser();

            Product product = productSer.findById(pid);

            cartItemSer.removeProductAndUser(pid, user);
            return new ResponseEntity<>(pid, HttpStatus.OK);
        } catch (ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}

