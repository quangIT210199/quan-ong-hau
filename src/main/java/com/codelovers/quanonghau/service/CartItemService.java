package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.User;

import java.util.List;

public interface CartItemService {

    List<CartItem> listCartItems(User user);

    // add product to Shopping Cart
    Integer addProduct(Integer productId, Integer quantity, User user);

    float updateQuantity(Integer quantity, Integer productId, User user);

    void removeProductAndUser(Integer productId, User user);

    void updateBillId(Integer billId, Integer[] cartIds);

    CartItem findByIdAndUser(Integer cartID, Integer userId);

    CartItem exitBillId(Integer productId, User user);
}
