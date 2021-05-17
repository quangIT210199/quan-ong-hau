package com.codelovers.quanonghau.configs.jwt;

import com.codelovers.quanonghau.configs.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j
@Transactional
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Lấy jwt khi có request đi qua filter
            String jwt = getJwtFromRequest(request);
            System.out.println("jwt client: " + jwt);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                System.out.println("vch @@ jwt client gửi nè: " + jwt);
                //Vì tích hợp id vô token lên cần lấy id ra
                Integer userId = tokenProvider.getUserIdFromJWT(jwt);

                // Tìm user theo id và convert thành UserDetails
                UserDetails userDetails = userDetailsService.loadUserById(userId); // để thực hiện Authen

                System.out.println(userDetails.getAuthorities());

                if (userDetails != null) {
                    // Nếu người dùng hợp lệ, set thông tin cho Security Context

                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails
                                    .getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("failed on set user authentication", ex);
        }
        System.out.println("Chạy filter để chuyển tới Controller");
        filterChain.doFilter(request, response); // 4
    }

    private String getJwtFromRequest(HttpServletRequest request) {
//        String token = "";
//
//        Cookie[] cookies = null;
//        cookies = request.getCookies();
//        for (Cookie cookie : cookies){
//            if (cookie.getName().equals(Contrants.TOKEN)) {
//                token = cookie.getValue();
//            }
//        }
//
//        if(token != null) {
//            return token;
//        }

        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
