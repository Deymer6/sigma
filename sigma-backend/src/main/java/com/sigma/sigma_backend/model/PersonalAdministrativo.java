package com.sigma.sigma_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Personal_Administrativo") // <-- Coincide con el SQL
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalAdministrativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personal")
    private Long idPersonal;

    // --- ¡AQUÍ ESTÁ LA RELACIÓN! ---
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
    // ---------------------------------

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String puesto; // <-- Coincide con el SQL
}