package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {

    List<HistorialClinico> findByPacienteId(Long pacienteId);
    List<HistorialClinico> findByPaciente_IdPacienteOrderByFechaAtencionDesc(Long idPaciente);
}
