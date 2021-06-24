package com.codelovers.quanonghau.controller;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.controller.input.PasswordReset;
import com.codelovers.quanonghau.dto.PasswordDTO;
import com.codelovers.quanonghau.exception.PasswordResetTokenNotFoundException;
import com.codelovers.quanonghau.models.Role;
import com.codelovers.quanonghau.models.User;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.configs.jwt.JwtTokenProvider;
import com.codelovers.quanonghau.configs.payload.LoginRequest;
import com.codelovers.quanonghau.configs.payload.LoginResponse;
import com.codelovers.quanonghau.configs.payload.SignupRequest;
import com.codelovers.quanonghau.service.RoleService;
import com.codelovers.quanonghau.service.UserService;
import com.codelovers.quanonghau.utils.MailUtil;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Storegare and get authorites in here

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userSer;

    @Autowired
    private RoleService roleSer;

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

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
        System.out.println("Login success");
        return new ResponseEntity<>(new LoginResponse(jwt,
                customUserDetails.getUser().getId(), customUserDetails.getUsername(), roles), HttpStatus.OK);
    }

    @GetMapping(value = "/register", produces = "application/json")
    public ResponseEntity<?> viewRegisterForm() {

        return new ResponseEntity<>(new SignupRequest(), HttpStatus.OK);
    }

    //For User, need code @Valid
    @PostMapping(value = "/create_user", produces = "application/json")
    public ResponseEntity<?> createUser(@Validated @RequestBody SignupRequest signupRequest, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

        if (userSer.exitUserByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        User user = new User(signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getFirstName(), signupRequest.getLastName());
        userSer.registerUser(user);

        System.out.println("Verify Code: " + user.getVerificationCode());
        sendVerificationEmail(request, user);

        return new ResponseEntity<>("Registration success", HttpStatus.OK);
    }

    // Tạo USER mới để test
    @PostMapping(value = "/create", produces = "application/json")
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

    private void sendVerificationEmail(HttpServletRequest request, User user) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Contrants.USER_VERIFY_SUBJECT;
        String content = Contrants.USER_VERIFY_CONTENT;

        MimeMessage message = mailSender.createMimeMessage(); // interface to create MIME
        MimeMessageHelper help = new MimeMessageHelper(message); // a class support create MIME with image,audio or html

        help.setFrom(Contrants.MAIL_FROM, Contrants.MAIL_SENDER_NAME);
        help.setTo(toAddress);
        help.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());

        String verifyURL = MailUtil.getSiteURL(request, "/create_user") + "/verify?code=" + user.getVerificationCode();
        System.out.println(verifyURL);
        content = content.replace("[[URL]]", verifyURL);

        help.setText(content, true);
//        message.setContent(content, "text/html");
        mailSender.send(message);

        System.out.println("to Address: " + toAddress);
        System.out.println("Verify URL: " + verifyURL);
    }

    @GetMapping(value = "/verify", produces = "application/json")
    public ResponseEntity<?> verifyAccount(@RequestParam(name = "code") String code) {

        boolean verified = userSer.verifyCode(code);

        String result = verified ? "Congratulations! Your account has been verified." : "Your account was already verified, or the verification code is invalid";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Forgot Password API for App
    @PostMapping(value = "/reset_password", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordReset email) throws UnsupportedEncodingException {
        User user = userSer.getUserByEmail(email.getEmail());

        if (user == null) {
            return new ResponseEntity<>("There were an error", HttpStatus.NOT_FOUND);
        }

        String newPassword = userSer.resetPassword(user);
        System.out.println("Mat khau moi la: " + newPassword);
        try {
            sendResetPasswordEMail(newPassword, user);
            return new ResponseEntity<>(email,HttpStatus.OK);
//            return new ResponseEntity<>("Your password has been reset. Please check your e-mail.",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("There were an error",HttpStatus.OK);
        }
    }

    private void sendResetPasswordEMail(String newPassword, User user) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Contrants.RESET_PASSWORD_SUBJECT;
        String content = Contrants.RESET_PASSWORD_CONTENT;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(Contrants.MAIL_FROM, Contrants.MAIL_SENDER_NAME);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        content = content.replace("[[newPassword]]", newPassword);

        helper.setText(content, true);
        mailSender.send(message);
        System.out.println("to Address: " + toAddress);
    }

    // Reset Password in web
    @PostMapping(value = "/password_reset_token", produces = "application/json")
    public ResponseEntity<?> resetPasswordToken(HttpServletRequest request, @RequestBody PasswordReset email) throws MessagingException, UnsupportedEncodingException {
        User user = userSer.getUserByEmail(email.getEmail());

        if (user == null) {
            return new ResponseEntity<>("There were an error", HttpStatus.NOT_FOUND);
        }

        String token = RandomString.make(64);
        userSer.createPasswordResetTokenForUser(token, user);

        sendURLPasswordResetToken(request, token, user);

        return new ResponseEntity<>("Your link reset password has been send. Please check your e-mail.",HttpStatus.OK);
    }

    @PostMapping(value = "/save_password", produces = "application/json")
    public ResponseEntity<?> savePassword(@Valid @RequestBody PasswordDTO passwordDTO) throws PasswordResetTokenNotFoundException {
        String result = userSer.validatePasswordResetToken(passwordDTO.getToken());
        System.out.println(passwordDTO.toString());
        if (result != null) {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }

        User user = userSer.getUserByPasswordResetToken(passwordDTO.getToken());
        if (user == null) {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }

        userSer.changePassword(user, passwordDTO.getNewPassword());
        // Delete PasswordReset, need fix
        userSer.deletePasswordResetToken(passwordDTO.getToken());

        return new ResponseEntity<>(passwordDTO, HttpStatus.OK);
    }

    private void sendURLPasswordResetToken(HttpServletRequest request, String token, User user) throws MessagingException, UnsupportedEncodingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Contrants.RESET_PASSWORD_WEB_SUBJECT;
        String content = Contrants.RESET_PASSWORD_WEB_CONTENT;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(Contrants.MAIL_FROM, Contrants.MAIL_SENDER_NAME);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());

        String resetURL = MailUtil.getSiteURL(request, "/api/auth/password_reset_token") + "/forgot_pass?token=" + token;
        content = content.replace("[[URL]]", resetURL);

        helper.setText(content, true);
        mailSender.send(message);

        System.out.println("to Address: " + toAddress);
        System.out.println("Reset URL: " + resetURL);
    }
}