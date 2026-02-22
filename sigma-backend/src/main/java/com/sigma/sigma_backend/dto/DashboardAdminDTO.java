package com.sigma.sigma_backend.dto;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardAdminDTO {
    private long totalObstetras;
    private long totalPacientes;
    private long citasEsteMes;
    private double porcentajeActivos; 
    
    
    private Map<String, Long> citasPorMes; 
    
    // Para la lista de actividad reciente (simple strings por ahora)
    private List<String> actividadReciente;
}