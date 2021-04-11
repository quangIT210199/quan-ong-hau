package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo;

    @Override
    public User findById(Integer id) {

        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.getUserByEmail(email);
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
    public boolean exitUserByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public boolean exitUserByUserName(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public User createdUser(User user) {
        // Need validate fields of User
        return userRepo.save(user);
    }
}
