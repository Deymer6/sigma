package com.sigma.sigma_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Obstetras") 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Obstetra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obstetra")
    private Long idObstetra;

    
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
  

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "num_colegiatura", nullable = false, unique = true) // <-- Coincide con el SQL
    private String numColegiatura;

    private String especialidad;

    @Column(name = "telefono_contacto") // <-- Coincide con el SQL
    private String telefonoContacto;

    public Long getId() {
        return idObstetra;
    }
}