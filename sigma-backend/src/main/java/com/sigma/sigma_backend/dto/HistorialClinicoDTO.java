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
public class HistorialClinicoDTO {
    private Long idHistorial;
    private Long pacienteId;
    private Long obstetraId;
    private Long citaId;
    private LocalDateTime fechaAtencion;
    private String diagnostico;
    private String prescripciones;
    private String antecedentesMedicos;
    private String obstetraNombre;
}