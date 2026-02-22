package com.sigma.sigma_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Pacientes") 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    // --- ¡AQUÍ ESTÁ LA RELACIÓN! ---
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
    // ---------------------------------

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "documento_identificacion", unique = true) // <-- Coincide con el SQL
    private String documentoIdentificacion;

    private String sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    // (Añadimos los otros campos de nuestro diseño SQL)
    @Column(name = "fecha_probable_parto")
    private LocalDate fechaProbableParto;

    @Column(name = "estado_gestacional")
    private String estadoGestacional;

    public Long getId() {
        return idPaciente;
    }
}