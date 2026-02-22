package com.sigma.sigma_backend.controller;



import com.sigma.sigma_backend.dto.DashboardPacienteDTO;
import com.sigma.sigma_backend.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/dashboard-info")
    @PreAuthorize("hasAuthority('ROL_PACIENTE')")
    public ResponseEntity<DashboardPacienteDTO> getDashboardInfo() {
        // 1. Obtener el email/usuario del token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2. Llamar al servicio para armar el dashboard
        DashboardPacienteDTO dashboardData = pacienteService.obtenerDatosDashboard(email);
        
        return ResponseEntity.ok(dashboardData);
    }
}