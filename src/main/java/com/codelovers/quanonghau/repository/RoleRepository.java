package com.codelovers.quanonghau.repository;

import com.codelovers.quanonghau.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String roleName);
}
