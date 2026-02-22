package com.sigma.sigma_backend.dto;

import lombok.Data; 
import java.time.LocalDate;
import java.util.List;

@Data
public class DashboardPacienteDTO {
    private Long idPaciente;
    private String pacienteNombre;
    private EmbarazoResumenDTO embarazo;
    private CitaResumenDTO proximaCita;
    private ObstetraResumenDTO obstetra;
    private List<ResultadoResumenDTO> ultimosResultados;
    private List<IndicacionResumenDTO> indicaciones;

    
    
    @Data
    public static class EmbarazoResumenDTO {
        private int semanas;
        private long diasFaltantes;
        private String trimestre;
        private LocalDate fechaProbableParto;
        private int porcentajeProgreso;
    }

    @Data
    public static class CitaResumenDTO {
        private LocalDate fecha;
        private String hora; // O LocalTime
        private String doctorNombre;
        private String especialidad;
    }

    @Data
    public static class ObstetraResumenDTO {
        private String nombre;
        private String especialidad;
        private String fotoUrl;
        private String telefono;
        private String whatsapp;
    }

    @Data
    public static class ResultadoResumenDTO {
        private String nombre;
        private LocalDate fecha;
        private String archivoUrl;
        private String tipo; // "pdf" o "imagen"
    }

    @Data
    public static class IndicacionResumenDTO {
        private String medicamento;
        private String dosis;
    }
}