package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User findById(Integer id) {

        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User getCurrentlyLoggedInUser(Authentication authentication){
        if(authentication == null)
            return null;

        User user = null;
        Object principal = authentication.getPrincipal();

        if(principal instanceof CustomUserDetails){
            user = ((CustomUserDetails) principal).getUser();
        }

        return user;
    }

    @Override
    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.getUserByEmail(email);

        if(userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
            if (userByEmail != null) return false;
        }
        else {
            if(userByEmail.getId() != id){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean exitUserByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public User createdUser(User user) {
        // Need validate fields of User
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            User existingUser = userRepo.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }

        } else {
            encodePassword(user);
        }

        return userRepo.save(user);
    }

    @Override
    public User updateUser(User user) {

        return userRepo.save(user);
    }

    private void encodePassword(User user) {
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
