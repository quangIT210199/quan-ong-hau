package com.codelovers.quanonghau.dto;

import lombok.Data;

@Data
public class PasswordDTO {
    private String oldPassword;

    private  String token;

//    @ValidPassword
    private String newPassword;
}
