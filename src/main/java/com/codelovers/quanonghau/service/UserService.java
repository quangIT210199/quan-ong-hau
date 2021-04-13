package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface UserService {

    User findById(Integer id);

    User getCurrentlyLoggedInUser(Authentication authentication);

    boolean isEmailUnique(Integer id,String email);

    boolean exitUserByEmail(String email);

    User createdUser(User user);

    void updateUserEnabledStatus(Integer id, boolean enabled);

    void deleteUser(Integer id) throws UserNotFoundException;

    Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword);
}
