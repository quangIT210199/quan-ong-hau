package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.User;

import java.util.List;

public interface CartItemService {

    List<CartItem> listCartItems(User user);

    // add product to Shopping Cart
    Integer addProduct(Integer productId, Integer quantity, Integer userId);

    float updateQuantity(Integer quantity, Integer productId, Integer userId);

    void removeProductAndUser(Integer productId, Integer userId);
}
