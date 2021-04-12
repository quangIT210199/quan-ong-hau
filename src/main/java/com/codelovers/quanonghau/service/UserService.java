package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {

    User findById(Integer id);

    User getCurrentlyLoggedInUser(Authentication authentication);

    boolean isEmailUnique(Integer id,String email);

    boolean exitUserByEmail(String email);

    User createdUser(User user);

    User updateUser(User user);
}
