package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.models.Role;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import com.codelovers.quanonghau.repository.UserRepository;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional // Because we using query update/delete in Repository
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleService roleSer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findById(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (NoSuchElementException ex){
            throw new UserNotFoundException("Could not found user with id: " + id);
        }
    }

    @Override
    public List<User> listAll() { // For PDF
        return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    @Override // Chưa được dùng
    public User getCurrentlyLoggedInUser(Authentication authentication) {
        if (authentication == null)
            return null;

        User user = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
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
    public boolean checkIfValidOldPassword(User user, String oldPassword) {

        passwordEncoder = new BCryptPasswordEncoder();

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            return true;
        }

        return false;
    }

    @Override
    public void changePassword(User user, String newPassword) {

        userRepo.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }

    @Override
    public boolean isEmailUnique(Integer id, String email) { // Use for update User
        User userByEmail = userRepo.getUserByEmail(email);

        if (userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            if (userByEmail != null) return false;
        } else {
            if (userByEmail.getId() != id) {
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

            user.setUpdateTime(new Date());
        } else {
            encodePassword(user);
            user.setCreateTime(new Date());
        }

        return userRepo.save(user);
    }

    // Using in AccountController
    @Override
    public User updateAccount(User userInForm) {
        User userInDB = userRepo.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getPhotos() != null) {
            userInDB.setPhotos(userInForm.getPhotos());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepo.save(userInDB);
    }
    ////////////////

    @Override
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }

    @Override
    public void deleteUser(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find user with id: " + id);
        }

        userRepo.deleteById(id);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    /////////////////// THIS method using for USER like Customer
    @Override
    public void registerUser(User user) {
        Set<Role> roles = new HashSet<>();

        Role userRole = roleSer.findByName(Contrants.USER);
        roles.add(userRole);

        user.setRoles(roles);

        encodePassword(user);
        user.setEnabled(false);
        user.setCreateTime(new Date());

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);

        userRepo.save(user);
    }

    @Override
    public boolean verifyCode(String verificationCode) {
        User user = userRepo.getUserByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        }
        else {
            userRepo.enable(user.getId());
            return true;
        }
    }
}
