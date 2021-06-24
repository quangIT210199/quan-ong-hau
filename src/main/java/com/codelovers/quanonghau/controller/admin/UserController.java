package com.codelovers.quanonghau.controller.admin;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.output.admin.PagingUser;
import com.codelovers.quanonghau.models.CartItem;
import com.codelovers.quanonghau.models.Role;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.exception.UserNotFoundException;
import com.codelovers.quanonghau.export.UserPdfExporter;
import com.codelovers.quanonghau.configs.payload.SignupRequest;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import com.codelovers.quanonghau.utils.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This Controlle using for manager User
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userSer;

    @Autowired
    RoleService roleSer;

    @GetMapping(value = "/users", produces = "application/json")
    public ResponseEntity<?> listAllUser() {
        return new ResponseEntity<>(userSer.listAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/user/firstPage", produces = "application/json")
    public ResponseEntity<?> listFirstPage() {
        return listUser(1, "firstName", "asc", null);
    }


    @GetMapping(value = "/user/page", produces = "application/json")
    public ResponseEntity<?> listUser(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "sortField") String sortField,
                                      @RequestParam(value = "sortDir") String sortDir, @RequestParam("keyword") String keyword) {
        Page<User> page = userSer.listByPage(pageNum, sortField, sortDir, keyword);

        List<User> listUser = page.getContent();
        long startCount = (pageNum - 1) * Contrants.USERS_PER_PAGE + 1;// Start at index element
        long endCount = startCount + Contrants.USERS_PER_PAGE - 1; // End element

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        PagingUser pagingUser = new PagingUser();

        pagingUser.setUserList(listUser);
        pagingUser.setCurrentPage(pageNum);
        pagingUser.setTotalPage(page.getTotalPages());
        pagingUser.setStartCount(startCount);
        pagingUser.setEndCount(endCount);
        pagingUser.setTotalItems(page.getTotalElements());
        pagingUser.setSortField(sortField);
        pagingUser.setSortDir(sortDir);
        pagingUser.setKeyword(keyword);
        pagingUser.setReverseSortDir(reverseSortDir);

        return new ResponseEntity<>(pagingUser, HttpStatus.OK);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<?> getUserById(@RequestParam("id") Integer id) {
        try {
            User user = userSer.findById(id);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get information for form Create User
    @GetMapping(value = "/user/new", produces = "application/json")
    public ResponseEntity<?> newUser() {
        List<Role> listRole = roleSer.listRole();

        User user = new User();
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();

        for (Role role : listRole) {
            roles.add(role);
        }

        user.setRoles(roles);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Tạo USER mới để test
    @PostMapping(value = "/user/create", produces = "application/json")
    public ResponseEntity<?> createUser(@Validated @RequestBody SignupRequest signupRequest) {
        if (userSer.exitUserByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = new User(signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getFirstName(), signupRequest.getLastName());

        Set<String> listRole = signupRequest.getRole();

        Set<Role> roles = new HashSet<>();

        if (listRole == null) {
            Role userRole = roleSer.findByName(Contrants.USER);
            roles.add(userRole);
        } else {
            listRole.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role roleAdmin = roleSer.findByName(Contrants.ADMIN);
                        roles.add(roleAdmin);
                        break;
                    default:
                        Role userRole = roleSer.findByName(Contrants.USER);
                        roles.add(userRole);
                        break;
                }
            });
        }

        user.setRoles(roles);
        user.setEnabled(true);

        userSer.createdUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //can use @RequestParam("image"), this API create User By ADMIN or update USER
    // User user is must input form-data
    @PostMapping(value = "/user/save", consumes = {"multipart/form-data"}, produces = "application/json")
    public ResponseEntity<?> saveUser(String userJson, @RequestParam(name = "imageFile") MultipartFile file) throws IOException {
        Gson gson = new Gson();
        User user = gson.fromJson(userJson, User.class);

        User savedUser = null;
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            user.setPhotos(fileName);
            savedUser = userSer.createdUser(user);

            String uploadDir = "images/user-photo/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);

        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            savedUser = userSer.createdUser(user);
        }

        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    // Get user information for edit form for USER , need code DTO for send Object json
    @GetMapping(value = "/user/edit/{id}", produces = "application/json")
    public ResponseEntity<?> editUser(@PathVariable(name = "id") Integer id) {
        try {
            User user = userSer.findById(id);

            List<Role> listRole = roleSer.listRole();

            Set<Role> roles = new HashSet<>();

            for (Role role : listRole) {
                roles.add(role);
            }

            user.setRoles(roles);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/delete", produces = "application/json")
    public ResponseEntity<?> removeUser(@RequestParam("id") Integer id) {
        try {
            userSer.deleteUser(id);
            String userDir = "images/user-photo/" + id;

            FileUploadUtil.removeDir(userDir);
            return new ResponseEntity<>("Delete success User", HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/{id}/enabled/{status}", produces = "application/json")
    public ResponseEntity<?> updateUserEnabledStatus(@PathVariable("id") Integer id,
                                                         @PathVariable("status") boolean enabled) {
        try {
            User user = userSer.findById(id);

            userSer.updateUserEnabledStatus(id, enabled);

            String status = enabled ? "enabled" : "disabled";

            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/user/check_email", produces = "application/json")
    public ResponseEntity<?> checkDuplicateEmail(@RequestParam("id") Integer id, @RequestParam("email") String email) {

        String result = userSer.isEmailUnique(id, email) ? "OK" : "Duplicated";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /*PDF*/
//    @GetMapping(value = "/user/export/pdf", produces = {"application/octet-stream"})
//    public ResponseEntity<?> exportToPDF() throws IOException {
//        List<User> listUsers = userSer.listAll();
//        UserPdfExporter exporter = new UserPdfExporter();
//
//        ByteArrayOutputStream dsf = new ByteArrayOutputStream();
//        exporter.export(listUsers, dsf);
//
//        ByteArrayResource resource = new ByteArrayResource(dsf.toByteArray());
//
//        return new ResponseEntity<>(resource, HttpStatus.OK);
//    }

    /*PDF*/
    @GetMapping("/user/export/pdf")
    public ResponseEntity<?> exportToPDF(HttpServletResponse response) throws IOException {
        List<User> listUsers = userSer.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
