package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.AnalisisClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalisisClinicoRepository extends JpaRepository<AnalisisClinico, Long> {
    
    List<AnalisisClinico> findByHistorialIdHistorial(Long historialId);
    @Query("SELECT a FROM AnalisisClinico a WHERE a.historial.paciente.idPaciente = :pacienteId ORDER BY a.fechaRealizacion DESC")
    List<AnalisisClinico> findAnalisisPorPacienteId(@Param("pacienteId") Long pacienteId);
    List<AnalisisClinico> findByHistorial_IdHistorial(Long idHistorial);

    // Busca los últimos 3 análisis del paciente ordenados por fecha
    List<AnalisisClinico> findTop3ByHistorial_Paciente_IdPacienteOrderByFechaRealizacionDesc(Long idPaciente);
}