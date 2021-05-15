package com.codelovers.quanonghau.security.payload;

import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> role;
}
