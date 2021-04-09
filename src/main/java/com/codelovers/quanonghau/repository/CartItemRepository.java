package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Bill;
import com.codelovers.quanonghau.entity.CartItem;
import com.codelovers.quanonghau.entity.Product;
import com.codelovers.quanonghau.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query(value = "SELECT * FROM cart_item u WHERE u.user_id = ?1 AND u.bill_id IS NULL",
            nativeQuery = true)
    List<CartItem> findAllByUser_Id(int id);

    CartItem findByProductAndUserAndBill(Product product, User user, Bill bill);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 "
            + "AND c.user.id = ?3")
    @Modifying
    void updateQuantity(Integer quantity, Integer productId, Integer userId);

    @Query("UPDATE CartItem c SET c.bill.id = ?1 WHERE c.id =?2")
    @Modifying
    void updateBillId(Integer billId, Integer cartId);

    @Query("DELETE FROM CartItem c WHERE c.product.id = ?1 AND c.user.id = ?2")
    @Modifying
    void deleteProductAndUser(Integer productId, Integer userId);

    CartItem findByIdAndUser(Integer cartItemId, User user);
}
