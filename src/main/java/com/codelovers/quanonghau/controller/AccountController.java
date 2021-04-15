package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.controller.output.UpdatePassword;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.entity.UserImage;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import com.codelovers.quanonghau.util.HandlingByte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// This Controller get detail user for update, need code DTO for API change Password
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private UserService userSer;

    @GetMapping(value = "/account", produces = "application/json")
    public ResponseEntity<?> viewDetail(@AuthenticationPrincipal CustomUserDetails loggerUser) {

        String email = loggerUser.getUsername();

        User user = userSer.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/account/updateInfo", produces = "application/json")
    public ResponseEntity<?> saveDetail(User user, @AuthenticationPrincipal CustomUserDetails loggerUser,
                                        @RequestParam(name = "imageFile") MultipartFile file) throws IOException {
        // Only update, get id
        User uTemp = loggerUser.getUser();
        // Cập nhật thông tin cá nhân cho user hiện tại
        uTemp.setLastName(user.getLastName());
        uTemp.setFirstName(user.getFirstName());
        uTemp.setAddress(user.getAddress());
        uTemp.setPhoneNumber(user.getPhoneNumber());

        if(!file.isEmpty()){
            UserImage img = new UserImage(file.getOriginalFilename(), file.getContentType(),
                    file.getBytes().length, HandlingByte.compressBytes(file.getBytes()));

            UserImage imageOld = userSer.findByUserId(uTemp.getId());
            if(imageOld != null) {

                System.out.println("ID của ảnh cũ: " + imageOld.getId());

                //get ImageOld and delete
                userSer.deleteUserImage(imageOld);
            }

            // SAVE imageNew
            img.setUser(uTemp);
            userSer.saveUserImage(img);
        }

        userSer.createdUser(uTemp);

        return new ResponseEntity<>(uTemp, HttpStatus.OK);
    }

    @PostMapping(value = "/account/updatePass", produces = "application/json")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePassword password,
                                            @AuthenticationPrincipal CustomUserDetails loggerUser){
        if(!password.getConfirmPass().equals(password.getNewPassword())){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println("TK đang logger: " + loggerUser.getUser().getEmail());
        System.out.println("Ten ng dang login: " + SecurityContextHolder.getContext().getAuthentication().getName());

        if(userSer.checkIfValidOldPassword(loggerUser.getUser(), password.getOldPassword())){

            userSer.changePassword(loggerUser.getUser(), password.getNewPassword());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
