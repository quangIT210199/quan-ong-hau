package com.codelovers.quanonghau.repository;

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

    @Query(value = "SELECT * FROM cart_item u WHERE u.user_id = ?1",
            nativeQuery = true)
    List<CartItem> findAllByUser_Id(int id);

    CartItem findByProductAndUser(Product product, User user);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 "
            + "AND c.user.id = ?3")
    @Modifying
    void updateQuantity(Integer quantity, Integer productId, Integer userId);

    @Query("DELETE FROM CartItem c WHERE c.product.id = ?1 AND c.user.id = ?2")
    @Modifying
    void deleteProductAndUser(Integer productId, Integer userId);
}
