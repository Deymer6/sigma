package com.sigma.sigma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalisisClinicoDTO {

    private Long idAnalisis;
    
    
    private Long historialId; 

    private String tipoAnalisis;
    private LocalDate fechaRealizacion;
    private String valoresObservaciones;
    private String archivoAdjuntoUrl;
}