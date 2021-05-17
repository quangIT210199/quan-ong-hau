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
import com.codelovers.quanonghau.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
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

    @PostMapping(value = "/login", produces = "application/json")
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

    private void sendVerificationEmail(HttpServletRequest request, User user) throws UnsupportedEncodingException, MessagingException {
        JavaMailSenderImpl mailSender = MailUtil.prepareMailSender();

        String toAddress = user.getEmail();
        String subject = Contrants.USER_VERIFY_SUBJECT;
        String content = Contrants.USER_VERIFY_CONTENT;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper help = new MimeMessageHelper(message);

        help.setFrom(Contrants.MAIL_FROM, Contrants.MAIL_SENDER_NAME);
        help.setTo(toAddress);
        help.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());

        String verifyURL = MailUtil.getSiteURL(request) + "/verify?code=" + user.getVerificationCode();
        System.out.println(verifyURL);
        content = content.replace("[[URL]]", verifyURL);

        help.setText(content, true);

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
}