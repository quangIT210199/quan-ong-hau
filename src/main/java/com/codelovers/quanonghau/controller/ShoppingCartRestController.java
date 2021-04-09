package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.service.CartItemService;
import com.codelovers.quanonghau.service.ProductService;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
public class ShoppingCartRestController {

    @Autowired
    ProductService productSer;

    @Autowired
    UserService userSer;

    @Autowired
    CartItemService cartItemSer;

    // Show shopping Cart
    @GetMapping(value = "/cart/{uid}", produces = "application/json")
    public ResponseEntity<?> showShoppingCart(@PathVariable(name = "uid") Integer uid,
                                              @AuthenticationPrincipal Authentication authentication){
        // Need to code authen for get user instance
        //User user = userSer.getCurrentlyLoggedInUser(authentication);
        // And here need use uid
        User user = userSer.findById(uid);

        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<CartItem> cartItems = cartItemSer.listCartItems(user);

        if(cartItems.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen
    @GetMapping(value = "/cart/add/{pid}/{uid}/{qty}", produces = "application/json")
    public ResponseEntity<?> addProductToCart(@PathVariable(name = "pid") Integer pid, @PathVariable(name = "uid") Integer uid,
                                                @PathVariable(name = "qty") Integer qty){
        User user = userSer.findById(uid);
        if(user == null){ // check user
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Integer addedQuantity = cartItemSer.addProduct(pid, qty, uid);

        return new ResponseEntity<>(addedQuantity, HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}/{qty}
    // Calculator when click button
    @PostMapping(value = "/cart/update/{pid}/{uid}/{qty}", produces = "application/json")
    public ResponseEntity<?> updateQuantity(@PathVariable(name = "pid") Integer pid, @PathVariable(name = "uid") Integer uid,
                                            @PathVariable(name = "qty") Integer qty){
        User user = userSer.findById(uid);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        float subTotal = cartItemSer.updateQuantity(qty, pid, uid);
        System.out.println("SubTotal = " + subTotal);
        return new ResponseEntity<>(String.valueOf(subTotal), HttpStatus.OK);
    }

    // Fix code, beacase value uid dont need when code authen : /cart/update/{pid}
    @DeleteMapping(value = "/cart/remove/{pid}/{uid}", produces = "application/json")
    public ResponseEntity<?> removeProductFromCart(@PathVariable(name = "pid") Integer pid, @PathVariable(name = "uid") Integer uid){

        User user = userSer.findById(uid);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Product product = productSer.findById(pid);
        if(product == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        cartItemSer.removeProductAndUser(pid, uid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Checkout Shopping Cart

}

