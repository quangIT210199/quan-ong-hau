package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.controller.output.UpdatePassword;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.service.UserService;
import com.codelovers.quanonghau.utils.FileUploadUtil;
import com.google.gson.Gson;
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
    public ResponseEntity<?> saveDetail(String userJson, @RequestParam(name = "imageFile") MultipartFile file,
                                        @AuthenticationPrincipal CustomUserDetails loggerUser) throws IOException {
        // Dùng cho multipartFile phải vậy, vì n ko cho truyền cùng Object
        Gson gson = new Gson();
        User user = gson.fromJson(userJson, User.class);
        System.out.println(user.getId());
        User savedUser = null;
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            user.setPhotos(fileName);
            savedUser = userSer.updateAccount(user);

            String uploadDir = "images/user-photo/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            if (user.getPhotos() == null || user.getPhotos().isEmpty()) user.setPhotos(null);

            savedUser = userSer.updateAccount(user);
        }

        loggerUser.setFirstName(savedUser.getFirstName());
        loggerUser.setLastName(savedUser.getLastName());

        return new ResponseEntity<>(savedUser, HttpStatus.OK);
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
        System.out.println("Đổi thành công");
        return new ResponseEntity<>(password, HttpStatus.OK);
    }
}
