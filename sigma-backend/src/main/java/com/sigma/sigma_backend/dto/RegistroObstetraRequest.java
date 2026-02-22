package com.sigma.sigma_backend.dto;
import lombok.Data;

@Data
public class RegistroObstetraRequest {
    // Datos de Usuario
    private String email;
    private String password; // Contraseña temporal
    
    // Datos de Perfil
    private String nombre;
    private String apellido;
    private String dni;
    private String numColegiatura;
    private String especialidad;
    private String telefono;
}