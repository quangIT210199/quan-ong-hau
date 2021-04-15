package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Warning: dont have any field dont exits in db
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email);

    Boolean existsByEmail(String email);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id =?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);

    Page<User> findAll();

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',"
            + " u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);  // Fillter by keyword

    @Query("UPDATE User u SET u.password = ?2 WHERE u.id =?1")
    @Modifying
    User updatePassword(Integer id, String password);
}
