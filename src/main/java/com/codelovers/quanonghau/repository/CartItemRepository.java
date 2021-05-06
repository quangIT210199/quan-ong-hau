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
import java.util.Set;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query(value = "SELECT * FROM cart_item u WHERE u.user_id = ?1 AND u.bill_id IS NULL",
            nativeQuery = true)
    List<CartItem> findAllByUser_Id(int id);

    @Query(value = "SELECT * FROM cart_item c  WHERE c.product_id =?1 AND c.user_id = ?2 AND c.bill_id IS NULL", nativeQuery = true)
    CartItem exitBillId(Integer productId, Integer userId);

    CartItem findByProductAndUserAndBill(Product product, User user, Bill bill);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 "
            + "AND c.user.id = ?3")
    @Modifying
    void updateQuantity(Integer quantity, Integer productId, Integer userId);

    @Query("UPDATE CartItem c SET c.bill.id = ?1 WHERE c.id =?2")
    @Modifying
    void updateBillId(Integer billId, Integer cartId);

    // Delete Cart Item doesn't have Bill id
    @Query("DELETE FROM CartItem c WHERE c.product.id = ?1 AND c.user.id = ?2 AND c.bill.id IS NULL")
    @Modifying
    void deleteProductAndUser(Integer productId, Integer userId);

    CartItem findByIdAndUser(Integer cartItemId, User user);

    // Get Arrays Bill ids by User Id
    @Query(value = "SELECT c.bill_id FROM cart_item c WHERE c.user_id = ?1 AND C.bill_id IS NOT NULL", nativeQuery = true)
    Set<Integer> findBillsByUserId(Integer userId);
}
