package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.CustomUserDetails;
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
    public ResponseEntity<?> showShoppingCart( @AuthenticationPrincipal CustomUserDetails customUserDetails){

        if(customUserDetails == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Need to code authen for get user instance
        //User user = userSer.getCurrentlyLoggedInUser(authentication);
        // And here need use uid
        User user = customUserDetails.getUser();

        List<CartItem> cartItems = cartItemSer.listCartItems(user);

        if(cartItems.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen
    @GetMapping(value = "/cart/add/{pid}/{qty}", produces = "application/json")
    public ResponseEntity<?> addProductToCart(@PathVariable(name = "pid") Integer pid,
                                              @PathVariable(name = "qty") Integer qty, @AuthenticationPrincipal CustomUserDetails customUserDetails){
//        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
//            System.out.println("Null?");
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
        if(customUserDetails == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = customUserDetails.getUser();

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Integer addedQuantity = cartItemSer.addProduct(pid, qty, user);

        return new ResponseEntity<>(addedQuantity, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}/{qty}
    // Calculator when click button
    @PostMapping(value = "/cart/update/{pid}/{qty}", produces = "application/json")
    public ResponseEntity<?> updateQuantity(@PathVariable(name = "pid") Integer pid,
                                            @PathVariable(name = "qty") Integer qty, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        // Can check billID co ko. Co thi k dc tang quantity
        if(customUserDetails == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = customUserDetails.getUser();

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        CartItem c = cartItemSer.exitBillId(pid, user);
        if(c == null){ // Product is in Bill
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        float subTotal = cartItemSer.updateQuantity(qty, pid, user);
        System.out.println("SubTotal = " + subTotal);
        return new ResponseEntity<>(String.valueOf(subTotal), HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}
    @DeleteMapping(value = "/cart/remove/{pid}", produces = "application/json")
    public ResponseEntity<?> removeProductFromCart(@PathVariable(name = "pid") Integer pid, @AuthenticationPrincipal CustomUserDetails customUserDetails){

        if(customUserDetails == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = customUserDetails.getUser();

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        cartItemSer.removeProductAndUser(pid, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

