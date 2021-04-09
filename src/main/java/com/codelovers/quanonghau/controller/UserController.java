package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userSer;

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
}
