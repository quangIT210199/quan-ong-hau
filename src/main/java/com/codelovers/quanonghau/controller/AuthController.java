package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.Role;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.security.jwt.JwtTokenProvider;
import com.codelovers.quanonghau.security.payload.LoginRequest;
import com.codelovers.quanonghau.security.payload.LoginResponse;
import com.codelovers.quanonghau.security.payload.SignupRequest;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Storegare and get authorites in here

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userSer;

    @Autowired
    private RoleService roleSer;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

        // Validate username and password using spring authenticate
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            )
        );

        // If dont have exception -> set information to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gerenate JWT token and return to user
        String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new LoginResponse(jwt,
                customUserDetails.getUser().getId(), customUserDetails.getUsername(), roles), HttpStatus.OK);
    }

    //For User, need code @Valid
    @PostMapping(value ="/signup", produces = "application/json")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signupRequest){

        if(userSer.exitUserByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = new User(signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getFirstName(), signupRequest.getLastName());

        Set<Role> roles = new HashSet<>();

        Role userRole = roleSer.findByName(Contrants.USER);
        roles.add(userRole);

        user.setRoles(roles);
        user.setEnabled(true);
        userSer.createdUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //Forgot password
}
