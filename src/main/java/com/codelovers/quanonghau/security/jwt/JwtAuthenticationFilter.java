package com.codelovers.quanonghau.security.jwt;

import com.codelovers.quanonghau.security.UserDetailsServiceImpl;
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
            // Kiểm tra token null or empty và check jwt hợp lệ ko bằng JWT_SECRET
            // Nếu k vô if chạy thẳng dispathcher từ Http vào Controller tạo token
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
                    System.out.println("fail ở đây 3");
                }
            }
            System.out.println("nà ní");
        } catch (Exception ex) {
            log.error("failed on set user authentication", ex);
        }
        System.out.println("Chạy filter để chuyển tới Controller");
        filterChain.doFilter(request, response); // 4
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
