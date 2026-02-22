package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombreRol(String nombreRol);
}