package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findByToken(String token);

    Long countById(Integer id);
}
