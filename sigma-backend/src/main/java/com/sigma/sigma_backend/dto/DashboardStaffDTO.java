package com.sigma.sigma_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardStaffDTO {
    private Long idObstetra;
    private String nombreCompleto;
    private String especialidad;
    
    // Contadores
    private int cantidadCitasHoy;
    private int cantidadPacientesMes; 
    
    // La lista de hoy
    private List<CitaDiaDTO> citasDeHoy;

    @Data
    public static class CitaDiaDTO {
        private Long idCita;
        private String hora; 
        private String pacienteNombre;
        private String motivo;
        private String estado; 
    }
}