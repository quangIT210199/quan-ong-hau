package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.controller.output.UpdatePassword;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.UserService;
import com.codelovers.quanonghau.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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
    public ResponseEntity<?> saveDetail(User user, @RequestParam(name = "imageFile") MultipartFile file,
                                        @AuthenticationPrincipal CustomUserDetails loggerUser) throws IOException {
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            user.setPhotos(fileName);
            User savedUser = userSer.updateAccount(user);

            String uploadDir = "images/user-photo/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);

            userSer.updateAccount(user);
        }

        loggerUser.setFirstName(user.getFirstName());
        loggerUser.setLastName(user.getLastName());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/account/updatePass", produces = "application/json")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePassword password,
                                            @AuthenticationPrincipal CustomUserDetails loggerUser) {
        if (!password.getConfirmPass().equals(password.getNewPassword())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (userSer.checkIfValidOldPassword(loggerUser.getUser(), password.getOldPassword())) {

            userSer.changePassword(loggerUser.getUser(), password.getNewPassword());
        }

        return new ResponseEntity<>("Update password success", HttpStatus.OK);
    }
}
