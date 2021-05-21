package com.codelovers.quanonghau.controller.output;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.configs.CustomUserDetails;
import com.codelovers.quanonghau.configs.jwt.JwtTokenProvider;
import com.codelovers.quanonghau.configs.payload.LoginRequest;
import com.codelovers.quanonghau.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private UserService userSer;

    @Autowired
    private AuthenticationManager authenticationManager; // Storegare and get authorites in here

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(HttpServletRequest request, HttpServletResponse response, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("login", new LoginRequest());

            return "login";
        }

        return "redirect:home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String getHome(HttpServletRequest request, Model model) {
        String token = "";

        Cookie[] cookies = null;
        cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Contrants.TOKEN)) {
                token = cookie.getValue();
            }
        }

        model.addAttribute("jwt", token);

        return "index";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getQuang(HttpServletRequest request, Model model) {
        String token = "";

        Cookie[] cookies = null;
        cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Contrants.TOKEN)) {
                token = cookie.getValue();
            }
        }

        model.addAttribute("jwt", token);

        return "password-reset";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@ModelAttribute LoginRequest loginRequest,
                              HttpServletRequest request, HttpServletResponse response) {

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

        Cookie cookie = new Cookie(Contrants.TOKEN, jwt);

        response.addCookie(cookie);

        return "redirect:home";
    }

    @RequestMapping(value = "/forgot_pass", method = RequestMethod.GET)
    public String getView(@RequestParam(name = "token") String token, Model model) {

        String result = userSer.validatePasswordResetToken(token);
//        if(result != null) {
//            String message = messages.getMessage("auth.message." + result, null, locale);
//            return "redirect:/login.html?lang="
//                    + locale.getLanguage() + "&message=" + message;
//        } else {
//            model.addAttribute("token", token);
//            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
//        }

        return "password-reset.html";
    }
}
