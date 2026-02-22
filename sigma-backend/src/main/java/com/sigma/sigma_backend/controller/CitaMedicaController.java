package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.CitaMedicaDTO;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.repository.ObstetraRepository;
import com.sigma.sigma_backend.service.CitaMedicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaMedicaController {

    private final CitaMedicaService citaMedicaService;
    private final ObstetraRepository obstetraRepository;

    public CitaMedicaController(CitaMedicaService citaMedicaService, ObstetraRepository obstetraRepository) {
        this.citaMedicaService = citaMedicaService;
        this.obstetraRepository = obstetraRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROL_PACIENTE', 'ROL_OBSTETRA', 'ROL_ADMINISTRATIVO')")
    public ResponseEntity<?> crearCita(@RequestBody CitaMedicaDTO citaMedicaDTO, Authentication authentication) {
        
        String emailActual = authentication.getName();
        
        Obstetra obstetraLogueado = obstetraRepository.findByUsuarioEmail(emailActual).orElse(null);

        if (obstetraLogueado != null) {
            citaMedicaDTO.setObstetraId(obstetraLogueado.getIdObstetra());
        }

        if (citaMedicaDTO.getObstetraId() == null) {
            return ResponseEntity.badRequest().body("Error: No se ha especificado el obstetra para la cita.");
        }

        try {
            CitaMedicaDTO nuevaCita = citaMedicaService.crearCita(citaMedicaDTO);
            return ResponseEntity.ok(nuevaCita);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAuthority('ROL_PACIENTE') or hasAuthority('ROL_OBSTETRA')")
    public ResponseEntity<List<CitaMedicaDTO>> getCitasPorPaciente(@PathVariable Long pacienteId) {
        List<CitaMedicaDTO> citas = citaMedicaService.getCitasPorPaciente(pacienteId);
        return ResponseEntity.ok(citas);
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ROL_PACIENTE', 'ROL_OBSTETRA', 'ROL_ADMINISTRATIVO')")
    public ResponseEntity<CitaMedicaDTO> cancelarCita(@PathVariable Long id) {
        CitaMedicaDTO citaCancelada = citaMedicaService.cancelarCita(id);
        return ResponseEntity.ok(citaCancelada);
    }
}