package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
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
    public User getUserByEmail(String email) {
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
    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, Contrants.USERS_PER_PAGE, sort);

        if (keyword != null) { // find by Field using Concat
            return userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    @Override
    public boolean isEmailUnique(Integer id, String email) { // Use for update User
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
    public User createdUser(User user) { // Func này check cả update nhưng nên tách ra
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
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }

    @Override
    public void deleteUser(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);

        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find user with id: " + id);
        }

        userRepo.deleteById(id);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
