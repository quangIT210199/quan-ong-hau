package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {

    User findById(Integer id);

    User findByEmail(String email);

    User getCurrentlyLoggedInUser(Authentication authentication);

    boolean exitUserByEmail(String email);

    boolean exitUserByUserName(String username);

    User createdUser(User user);
}
