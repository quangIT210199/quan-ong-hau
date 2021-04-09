package com.codelovers.quanonghau.security;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 1. Load the user from the users table by username. If not found, throw UsernameNotFoundException.
        // 2. Convert/wrap the user to a UserDetails object and return it.
        // Tham số truyền vào chỉ có username người dùng
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepo.getUserByEmail(username);
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
//        return new CustomUserDetails(user);

//        return CustomUserDetails.build(user);
        return new CustomUserDetails(user);
    }

    @Transactional
    public UserDetails loadUserById(Integer id){ // used for JWT filter
        User user = userRepo.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return new CustomUserDetails(user);
    }

//    public User getCurrentlyLoggedInUser(Authentication authentication){
//        if(authentication == null)
//            return null;
//
//        User user = null;
//        Object principal = authentication.getPrincipal();
//
//        if(principal instanceof CustomUserDetails){
//            user = ((CustomUserDetails) principal).getUser();
//        }
//
//        return user;
//    }
}
