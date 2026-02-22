package com.sigma.sigma_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Historiales_Clinicos") // Coincide con el SQL
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    // --- ¡AQUÍ LAS CORRECCIONES DE LÓGICA! ---

    // 1. Un Paciente tiene MUCHOS historiales (relación Many-to-One)
    @ManyToOne
    @JoinColumn(name = "id_paciente", referencedColumnName = "id_paciente", nullable = false)
    private Paciente paciente;

    // 2. Un Obstetra registra MUCHOS historiales (Many-to-One)
    @ManyToOne
    @JoinColumn(name = "id_obstetra", referencedColumnName = "id_obstetra", nullable = false)
    private Obstetra obstetra;

    // 3. Un Historial se genera a partir de UNA Cita (One-to-One)
    @OneToOne
    @JoinColumn(name = "id_cita", referencedColumnName = "id_cita")
    private CitaMedica cita;

    // --- Campos de la visita (como en nuestro SQL) ---
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDateTime fechaAtencion;

    @Column(name = "diagnostico", columnDefinition = "NVARCHAR(MAX)")
    private String diagnostico;

    @Column(name = "prescripciones", columnDefinition = "NVARCHAR(MAX)")
    private String prescripciones;
}