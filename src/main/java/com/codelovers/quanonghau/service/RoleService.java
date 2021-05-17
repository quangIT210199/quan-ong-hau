package com.codelovers.quanonghau.service;

import com.codelovers.quanonghau.models.Role;

import java.util.List;

public interface RoleService {
    List<Role> listRole();

    Role findByName(String roleName);
}
