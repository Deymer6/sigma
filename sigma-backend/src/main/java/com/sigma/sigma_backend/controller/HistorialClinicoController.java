package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.HistorialClinicoDTO;
import com.sigma.sigma_backend.service.HistorialClinicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    public HistorialClinicoController(HistorialClinicoService historialService) {
        this.historialService = historialService;
    }

    /**
     * Endpoint para crear un nuevo registro de historial.
     * Solo un Obstetra puede crear un historial.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROL_OBSTETRA')")
    public ResponseEntity<HistorialClinicoDTO> crearHistorial(@RequestBody HistorialClinicoDTO dto) {
        HistorialClinicoDTO historialCreado = historialService.crearHistorial(dto);
        return ResponseEntity.ok(historialCreado);
    }

    /**
     * Endpoint para ver todos los historiales de un paciente.
     * Un Paciente puede ver sus historiales, y un Obstetra también.
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyAuthority('ROL_PACIENTE', 'ROL_OBSTETRA')")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialPorPaciente(@PathVariable Long pacienteId) {

        List<HistorialClinicoDTO> historiales = historialService.getHistorialPorPaciente(pacienteId);
        return ResponseEntity.ok(historiales);
    }

   
}