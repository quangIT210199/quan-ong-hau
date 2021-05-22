package com.codelovers.quanonghau.dto;

import lombok.Data;

@Data
public class PasswordDto {
    private String oldPassword;

    private  String token;

//    @ValidPassword
    private String newPassword;
}
