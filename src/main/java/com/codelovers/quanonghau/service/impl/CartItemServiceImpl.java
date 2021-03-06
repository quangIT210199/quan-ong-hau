package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.models.CartItem;
import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.models.User;
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

        // Cần check xem các Cart_item nào đã vào Bill chưa
        List<CartItem> cartItems = cartItemRepo.findAllByUserId(id);
//        for (CartItem cartItem: cartItems){
//            if(cartItem.getBill() != null){ // Đã tồn tại ở bill khác rồi
//                cartItems.remove(cartItem);
//            }
//        }

        return cartItems;
    }

    @Override
    public Integer addProduct(Integer productId, Integer quantity, User user) { // Change userId = User user when code authen
        Integer addedQuantity = quantity;

        Product product = productRepo.findById(productId).get();

        // Check xem giỏ hàng tồn tại và trạng thái có Bill chưa
        CartItem cartItem = cartItemRepo.findByProductAndUserAndBill(product, user, null);

        if (cartItem != null) { // giỏ hàng tồn tại
            addedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(addedQuantity);
        } else {
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
    public float updateQuantity(Integer quantity, Integer productId, User user) {// Change userId = User user when code authen
        cartItemRepo.updateQuantity(quantity, productId, user.getId());
        Product product = productRepo.findById(productId).get();

        float subTotal = product.getPrice() * quantity;

        return subTotal;
    }

    @Override
    public void removeProductAndUser(Integer productId, User user) {

        cartItemRepo.deleteProductAndUser(productId, user.getId());
    }

    @Override
    public void updateBillId(Integer billId, Integer[] cartIds) {
        System.out.println("Bill id: " + billId);

        for (Integer c : cartIds) {
            cartItemRepo.updateBillId(billId, c);
        }
    }

    @Override
    public CartItem findByIdAndUser(Integer cartID, Integer userId) {
        CartItem cartItem = cartItemRepo.findById(cartID).get();
        if (cartItem == null) {
            return null;
        }
        User user = userRepo.findById(userId).get();

        return cartItemRepo.findByIdAndUser(cartID, user);
    }

    @Override
    public CartItem exitBillId(Integer productId, User user) {
        return cartItemRepo.exitBillId(productId, user.getId());
    }
}
