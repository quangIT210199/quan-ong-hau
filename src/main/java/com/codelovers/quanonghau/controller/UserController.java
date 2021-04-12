package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.Role;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.payload.SignupRequest;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userSer;

    @Autowired
    RoleService roleSer;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<?> getUserById(@Param("id") Integer id){

        User user = userSer.findById(id);

        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Get information for form Create User
    @GetMapping(value = "/users/new", produces = "application/json")
    public ResponseEntity<?> newUser(){
        List<Role> listRole = roleSer.listRole();

        User user  = new User();
        user.setEnabled(true);
        user.setRoles((Set<Role>) listRole);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping(value = "/user/create", produces = "application/json")
    public ResponseEntity<?> createUser(@Validated @RequestBody SignupRequest signupRequest){

        if(userSer.exitUserByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = new User(signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()), signupRequest.getFirstName(), signupRequest.getLastName());

        Set<String> listRole = signupRequest.getRole();

        Set<Role> roles = new HashSet<>();

        for ( String name: listRole) {
            roles.add(new Role(name));
        }

        user.setRoles(roles);
        user.setEnabled(true);

        userSer.createdUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Need DTO
    @PutMapping(value = "/user/save/{id}", produces = "application/json")
    public ResponseEntity<?> saveUser(@PathVariable("id") Integer id ,@RequestBody User user){
        // Mỗi lần call cx phải check email
        User u = userSer.findById(id);

        if(u == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        userSer.updateUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Get user information for edit form, need DTO
    @GetMapping(value = "/user/edit/{id}", produces = "application/json")
    public ResponseEntity<?> editUser(@PathVariable(name = "id") Integer id){
        User user = userSer.findById(id);

        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/user/check_email", produces = "application/json")
    public ResponseEntity<?> checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email){
        String result =  userSer.isEmailUnique(id, email) ? "OK" : "Duplicated";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
