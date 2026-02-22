package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.Obstetra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ObstetraRepository extends JpaRepository<Obstetra, Long> {
    Optional<Obstetra> findByUsuarioEmail(String email);
}