package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.entity.Role;
import com.codelovers.quanonghau.entity.User;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.service.RoleService;
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
import java.util.HashSet;
import java.util.Set;

// This Controller get detail user for update, need code DTO for API change Password
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private UserService userSer;

    @Autowired
    private RoleService roleSer;

    @GetMapping(value = "/account", produces = "application/json")
    public ResponseEntity<?> viewDetail(@AuthenticationPrincipal CustomUserDetails loggerUser) {

        String email = loggerUser.getUsername();

        User user = userSer.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/account/update", produces = "application/json")
    public ResponseEntity<?> saveDetail(@RequestBody User user, @AuthenticationPrincipal CustomUserDetails loggerUser,
                                        MultipartFile multipartFile, String listRoles) throws IOException {
        Set<Role> roles = new HashSet<>();

        if(listRoles.isEmpty()){
            Role userRole = roleSer.findByName(Contrants.USER);
            roles.add(userRole);
        }
        else {
            String[] arr = listRoles.trim().split(",");
            for (String role : arr){
                if ("ADMIN".equals(role)) {
                    Role roleAdmin = roleSer.findByName(Contrants.ADMIN);
                    roles.add(roleAdmin);
                } else {
                    Role userRole = roleSer.findByName(Contrants.USER);
                    roles.add(userRole);
                }
            }
        }

        user.setRoles(roles);

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            System.out.println("Name of file: " + fileName);
            user.setPhotos(fileName);

            User userSave = userSer.createdUser(user);

            String uploadDir = "user-photos/" + userSave.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userSer.createdUser(user);
        }

        loggerUser.setFirstName(user.getFirstName());
        loggerUser.setLastName(user.getLastName());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
