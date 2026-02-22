package com.sigma.sigma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObstetraDTO {
    private Long id; 
    private String nombre;
    private String apellido;
    private String especialidad;
    private String numColegiatura; 
    private String email;
    private String estado;
}