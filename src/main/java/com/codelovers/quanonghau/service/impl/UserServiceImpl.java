package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
