package com.sigma.sigma_backend.repository;

import com.sigma.sigma_backend.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    List<CitaMedica> findByPacienteId(Long pacienteId);
    List<CitaMedica> findByObstetraId(Long obstetraId);
    long countByPacienteIdPacienteAndEstadoCita(Long idPaciente, String estadoCita);


    @Query("SELECT c FROM CitaMedica c WHERE c.obstetra.idObstetra = :idObstetra AND c.fechaCita BETWEEN :inicio AND :fin ORDER BY c.fechaCita ASC")
    List<CitaMedica> findCitasPorRango(@Param("idObstetra") Long idObstetra, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    long countByFechaCitaBetween(LocalDateTime inicio, LocalDateTime fin);
    // Buscar las Top 3 citas, de un paciente, que ya estén 'Realizada'ordenadas por fecha descendente
    List<CitaMedica> findTop3ByPaciente_IdPacienteAndEstadoCitaOrderByFechaCitaDesc(Long idPaciente, String estadoCita);

    @Query(value = "SELECT MONTH(fecha_cita) as mes, COUNT(*) as cantidad " +
                   "FROM Citas_Medicas " +
                   "WHERE YEAR(fecha_cita) = :anio " +
                   "GROUP BY MONTH(fecha_cita)", 
           nativeQuery = true)
    List<Object[]> contarCitasPorMes(@Param("anio") int anio);


}