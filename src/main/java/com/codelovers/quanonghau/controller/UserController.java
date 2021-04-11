package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.Role;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.payload.SignupRequest;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userSer;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Integer id){

        User user = userSer.findById(id);

        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email){

        User user = userSer.findByEmail(email);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/user/new", produces = "application/json")
    public ResponseEntity<?> createUser(@Validated @RequestBody SignupRequest signupRequest){
        if(userSer.exitUserByUserName(signupRequest.getUsername())){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if(userSer.exitUserByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = new User(signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()), signupRequest.getUsername());

        Set<String> listRole = signupRequest.getRole();

        Set<Role> roles = new HashSet<>();

        for ( String name: listRole
             ) {
            roles.add(new Role(name));
        }

        user.setRoles(roles);

        userSer.createdUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
