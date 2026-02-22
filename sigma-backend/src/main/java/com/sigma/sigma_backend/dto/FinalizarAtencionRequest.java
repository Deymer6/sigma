package com.sigma.sigma_backend.dto;
import lombok.Data;
import java.util.List;

@Data
public class FinalizarAtencionRequest {
    private Long idCita;
    private String diagnostico;
    private String observaciones;
    
    private List<MedicamentoDTO> receta;
    
    private String nombreArchivo;

    @Data
    public static class MedicamentoDTO {
        private String nombre;
        private String indicaciones;
    }
}