package com.codelovers.quanonghau.service.impl;

import com.codelovers.quanonghau.models.Role;
import com.codelovers.quanonghau.repository.RoleRepository;
import com.codelovers.quanonghau.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepo;

    @Override
    public List<Role> listRole() {
        return roleRepo.findAll();
    }

    @Override
    public Role findByName(String roleName) {

        return roleRepo.findByName(roleName);
    }
}
