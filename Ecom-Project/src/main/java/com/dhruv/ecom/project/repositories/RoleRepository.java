package com.dhruv.ecom.project.repositories;

import com.dhruv.ecom.project.Model.AppRole;
import com.dhruv.ecom.project.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {


    Optional<Role> findByRoleName(AppRole appRole);
}
