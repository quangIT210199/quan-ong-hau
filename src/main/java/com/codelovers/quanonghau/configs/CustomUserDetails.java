package com.codelovers.quanonghau.configs;

import com.codelovers.quanonghau.models.Role;
import com.codelovers.quanonghau.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    public static final String ROLE = "ROLE_";

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authories = new ArrayList<>();

        for (Role role : roles) {
            authories.add(new SimpleGrantedAuthority(ROLE + role.getName()));
        }

        return authories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { // Check user active?
        return user.isEnabled();
    }

    public String getFullName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }

    public void setFirstName(String firstName) {
        this.user.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        this.user.setLastName(lastName);
    }

    public boolean hasRole(String roleName) {
        return user.hasRole(roleName);
    }
}
