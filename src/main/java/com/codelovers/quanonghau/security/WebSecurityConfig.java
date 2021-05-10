package com.codelovers.quanonghau.security;

import com.codelovers.quanonghau.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // Get AuthenticationManager Bean
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Map userService.loadUserByUsername() for AuthenticationManager
        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder()); // Cung cấp encode password
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
//        http
//                .cors()
//                .and()
//                .csrf()
//                .disable()
//                .authorizeRequests()//// dont authenticate this particular request
//                .antMatchers("/api/login").permitAll()
//                .antMatchers("/api/v1/carts/**").hasAnyAuthority("USER","ADMIN")
//                .antMatchers("/api/v1/bills/**").hasRole("ADMIN")
//                .anyRequest().authenticated()//// all other requests need to be authenticated
//                .and()
//                .logout().permitAll();
//
//        // ADD Class filter to check JWT
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                // this disables session creation on Spring Security
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .cors() // Ngăn chặn request từ một domain khác
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/api/login").permitAll() // Cho phép tất cả mọi người truy cập vào địa chỉ này
                .antMatchers("/api/signup").permitAll()
                .antMatchers("/api/products/**").hasRole("ADMIN")
                .antMatchers("/api/categories/**").hasRole("ADMIN")
                .antMatchers("/api/users/**").hasRole("ADMIN")
                .antMatchers("/api/carts/**").hasAnyRole("USER","ADMIN")
                .antMatchers("/api/bills/**").hasRole("ADMIN")
                .anyRequest().authenticated()// Tất cả các request khác đều cần phải xác thực mới được truy cập
                .and()
                .logout()
                .permitAll();
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/login_page")
//                .permitAll()
//                .usernameParameter("email")
//                .passwordParameter("password");
//                .and()
//                .logout()
//                .invalidateHttpSession(true)
//                .deleteCookies("token");

        // Thêm một lớp Filter kiểm tra jwt
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // make sure we use stateless session; session won't be used to
        // store user's state.
//        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
    /*
    hasRole, hasAnyRole
    hasAuthority, hasAnyAuthority
    permitAll, denyAll
    isAnonymous, isRememberMe, isAuthenticated, isFullyAuthenticated : Login status of User
    principal, authentication
    hasPermission
    */
}
