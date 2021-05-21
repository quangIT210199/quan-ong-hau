package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    User findById(Integer id) throws UserNotFoundException;

    List<User> listAll();

    User getUserByEmail(String email);

    User getCurrentlyLoggedInUser(Authentication authentication);

    boolean isEmailUnique(Integer id, String email);

    boolean exitUserByEmail(String email);

    User createdUser(User user);

    void updateUserEnabledStatus(Integer id, boolean enabled);

    void deleteUser(Integer id) throws UserNotFoundException;

    Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    void changePassword(User user, String newPassword);

    User updateAccount(User userInForm);

    ////// This method using for USER like Customer
    void registerUser(User user);

    boolean verifyCode(String verificationCode);

    String resetPassword(User user);

    ///
    void createPasswordResetTokenForUser(String token, User user);

    String validatePasswordResetToken(String token);
}
