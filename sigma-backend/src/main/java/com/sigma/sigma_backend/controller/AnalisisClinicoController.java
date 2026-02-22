package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.AnalisisClinicoDTO;
import com.sigma.sigma_backend.service.AnalisisClinicoService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analisis")
public class AnalisisClinicoController {

    private final AnalisisClinicoService analisisService;

    public AnalisisClinicoController(AnalisisClinicoService analisisService) {
        this.analisisService = analisisService;
    }

    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROL_OBSTETRA')")
    public ResponseEntity<AnalisisClinicoDTO> subirAnalisis(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idHistorial") Long idHistorial,
            @RequestParam("tipoAnalisis") String tipoAnalisis,
            @RequestParam("observaciones") String observaciones,
            @RequestParam("fecha") String fechaStr // Recibimos fecha como String y parseamos
    ) {
        // Convertir fecha string (YYYY-MM-DD) a LocalDate
        LocalDate fecha = LocalDate.parse(fechaStr);

        AnalisisClinicoDTO nuevoAnalisis = analisisService.crearAnalisisConArchivo(
                idHistorial,
                tipoAnalisis,
                observaciones,
                fecha,
                file
        );

        return ResponseEntity.ok(nuevoAnalisis);
    }

    // Endpoint para ver análisis por historial clínico
    @GetMapping("/historial/{historialId}")
    @PreAuthorize("hasAnyAuthority('ROL_PACIENTE', 'ROL_OBSTETRA')")
    public ResponseEntity<List<AnalisisClinicoDTO>> getAnalisisPorHistorial(@PathVariable Long historialId) {
        List<AnalisisClinicoDTO> analisis = analisisService.getAnalisisPorHistorial(historialId);
        return ResponseEntity.ok(analisis);
    }

    // Endpoint para eliminar historial
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROL_ADMINISTRATIVO', 'ROL_OBSTETRA')") 
    public ResponseEntity<?> eliminarAnalisis(@PathVariable Long id) {
        analisisService.eliminarAnalisis(id);
        return ResponseEntity.noContent().build();
    }
    // Enpoin para listar todos los analisis
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyAuthority('ROL_PACIENTE', 'ROL_OBSTETRA')")
    public ResponseEntity<List<AnalisisClinicoDTO>> getAnalisisPorPaciente(@PathVariable Long pacienteId) {
        List<AnalisisClinicoDTO> analisis = analisisService.getAnalisisPorPaciente(pacienteId);
        return ResponseEntity.ok(analisis);
    }
}