package com.sigma.sigma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CitaMedicaDTO {
    private Long idCita;
    private Long pacienteId;
    private Long obstetraId;
    private String pacienteNombre;
    private String obstetraNombre;
    private LocalDateTime fechaCita;
    private String motivoConsulta;
    private String estadoCita;
}