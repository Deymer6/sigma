package com.sigma.sigma_backend.config;

import com.sigma.sigma_backend.model.Role;
import com.sigma.sigma_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByNombreRol("ROL_PACIENTE").isEmpty()) {
            roleRepository.save(Role.builder().nombreRol("ROL_PACIENTE").build());
        }
        if (roleRepository.findByNombreRol("ROL_OBSTETRA").isEmpty()) {
            roleRepository.save(Role.builder().nombreRol("ROL_OBSTETRA").build());
        }
        if (roleRepository.findByNombreRol("ROL_ADMINISTRATIVO").isEmpty()) {
            roleRepository.save(Role.builder().nombreRol("ROL_ADMINISTRATIVO").build());
        }
    }
}