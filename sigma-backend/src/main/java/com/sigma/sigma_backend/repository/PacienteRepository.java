package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByDocumentoIdentificacion(String dni);
    Optional<Paciente> findByUsuarioEmail(String email);

    List<Paciente> findTop50ByOrderByIdPacienteDesc();

    @Query("SELECT p FROM Paciente p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "p.documentoIdentificacion LIKE %:term%")
    List<Paciente> buscarPacientes(@Param("term") String term);
    
}