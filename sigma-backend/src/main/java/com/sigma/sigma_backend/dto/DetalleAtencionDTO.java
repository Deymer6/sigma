package com.sigma.sigma_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DetalleAtencionDTO {
    
    private Long idCita;
    private String horaCita; 
    
    
    private String pacienteNombre;
    private int edad; 
    private String semanasEmbarazo; 
    private String tipoSangre; 
    private String alergias;   
    
    // Historial (Lista simple de fechas anteriores)
    private List<String> ultimasConsultas; 
}