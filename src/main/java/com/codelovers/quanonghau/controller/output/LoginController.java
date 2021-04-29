package com.codelovers.quanonghau.controller.output;

import com.codelovers.quanonghau.contrants.Contrants;
import com.codelovers.quanonghau.security.CustomUserDetails;
import com.codelovers.quanonghau.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager; // Storegare and get authorites in here

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String getHome(HttpServletRequest request, Model model){
        String token = "";

        Cookie[] cookies = null;
        cookies = request.getCookies();
        for (Cookie cookie : cookies){
            if (cookie.getName().equals(Contrants.TOKEN)) {
                token = cookie.getValue();
            }
        }

        model.addAttribute("jwt", token);

        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@RequestParam String email, @RequestParam String password, Model model,
                            HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        // If dont have exception -> set information to Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gerenate JWT token and return to user
        String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());

        System.out.println(jwt);
        model.addAttribute("jwt", jwt);

        Cookie cookie = new Cookie(Contrants.TOKEN, jwt);

        response.addCookie(cookie);

        return "redirect:home";
    }

    @RequestMapping("logout")
    public String logoutForm(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Logout rồi");

        Cookie cookie = new Cookie("token", "");
        response.addCookie(cookie);

        return "login";
    }
}
