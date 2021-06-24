package com.codelovers.quanonghau.configs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthorityEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> errors = new HashMap<>();
//
//        Clock clock = Clock.systemDefaultZone();
//        Instant instant = clock.instant();
//
//        errors.put("timestamp", instant.toString());
//        errors.put("message", "Somethings wrong!");
//
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(401);
//        response.getWriter().write(objectMapper.writeValueAsString(errors));

        final String expired = (String) request.getAttribute("expired");
        System.out.println(expired);
        if (expired!=null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,expired);
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Login details");
        }

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                "You need to provide the JWT Token to Access This resource");
    }
}
