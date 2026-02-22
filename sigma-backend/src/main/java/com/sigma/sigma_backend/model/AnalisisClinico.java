package com.sigma.sigma_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Analisis_Clinicos") 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalisisClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_analisis")
    private Long idAnalisis;

    
    //  Un Analisis pertenece a UN Historial (Many-to-One)
    @ManyToOne
    @JoinColumn(name = "id_historial", referencedColumnName = "id_historial", nullable = false)
    private HistorialClinico historial;

    @Column(name = "tipo_analisis", nullable = false)
    private String tipoAnalisis;

    @Column(name = "fecha_realizacion")
    private LocalDate fechaRealizacion;

    @Column(name = "valores_observaciones", columnDefinition = "NVARCHAR(MAX)")
    private String valoresObservaciones;

    @Column(name = "archivo_adjunto_url")
    private String archivoAdjuntoUrl; 
}