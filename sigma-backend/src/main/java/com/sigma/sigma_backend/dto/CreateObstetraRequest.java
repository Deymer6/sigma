package com.sigma.sigma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateObstetraRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String numColegiatura;
    private String especialidad;
}