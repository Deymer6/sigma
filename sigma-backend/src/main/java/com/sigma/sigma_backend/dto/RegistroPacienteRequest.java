package com.sigma.sigma_backend.dto;

import lombok.Data;
import java.time.LocalDate; 

@Data
public class RegistroPacienteRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;
    
    
    private LocalDate fechaNacimiento; 
}