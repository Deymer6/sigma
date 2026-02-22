package com.sigma.sigma_backend.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PacienteCardDTO {
    private Long idPaciente;
    private String nombreCompleto;
    private String dni;
    private String ultimaVisita;
    private String email;
    private String estado; 
    private LocalDate fechaNacimiento;
}