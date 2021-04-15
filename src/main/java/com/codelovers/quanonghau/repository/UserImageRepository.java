package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Integer> {
    UserImage findByUserId(Integer id);
}
