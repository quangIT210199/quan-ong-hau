package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.CartItemRepository;
import com.codelovers.quanonghau.repository.ProductRepository;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    CartItemRepository cartItemRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    UserRepository userRepo;

    @Override
    public List<CartItem> listCartItems(User user) {

        int id = user.getId();
        List<CartItem> cartItems = cartItemRepo.findAllByUser_Id(id);
        return cartItems;
    }

    @Override
    public Integer addProduct(Integer productId, Integer quantity, Integer userId) { // Change userId = User user when code authen
        Integer addedQuantity = quantity;

        Product product = productRepo.findById(productId).get();

        User user = userRepo.findById(userId).get();

        CartItem cartItem = cartItemRepo.findByProductAndUser(product, user);

        if(cartItem != null){
            addedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(addedQuantity);
        }
        else{
            cartItem = new CartItem();
            cartItem.setQuantity(quantity);
            cartItem.setProduct(product);
            cartItem.setUser(user);
        }

        cartItemRepo.save(cartItem);

        return addedQuantity;
    }

    // increasing quantity when click plusBtn
    @Override
    public float updateQuantity(Integer quantity, Integer productId, Integer userId) {// Change userId = User user when code authen

        cartItemRepo.updateQuantity(quantity, productId, userId);
        Product product = productRepo.findById(productId).get();

        float subTotal = product.getPrice() * quantity;

        return subTotal;
    }

    @Override
    public void removeProductAndUser(Integer productId, Integer userId) {

        cartItemRepo.deleteProductAndUser(productId, userId);
    }
}
