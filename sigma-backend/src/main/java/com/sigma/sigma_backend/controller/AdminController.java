package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.DashboardAdminDTO; // <--- USAR ESTE DTO
import com.sigma.sigma_backend.dto.ObstetraDTO;
import com.sigma.sigma_backend.dto.PacienteCardDTO;
import com.sigma.sigma_backend.dto.RegistroObstetraRequest;
import com.sigma.sigma_backend.service.AdminService;
import com.sigma.sigma_backend.service.ObstetraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sigma.sigma_backend.dto.RegistroPacienteRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ObstetraService obstetraService;

    public AdminController(AdminService adminService, ObstetraService obstetraService) {
        this.adminService = adminService;
        this.obstetraService = obstetraService;
    }

    // DASHBOARD DEL ADMIN (Estadísticas Globales)
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAdminDTO> getDashboard() { // <--- CAMBIO AQUÍ
        return ResponseEntity.ok(adminService.obtenerDashboard());
    }

    // LISTAR OBSTETRAS (Para la tabla)
    @GetMapping("/obstetras")
    public ResponseEntity<List<ObstetraDTO>> listarObstetras() {
        // Reutilizamos el servicio que ya actualizamos para incluir email y estado
        return ResponseEntity.ok(obstetraService.listarTodos());
    }

    // CREAR NUEVO OBSTETRA (Para el modal)
    @PostMapping("/obstetras")
    public ResponseEntity<String> crearObstetra(@RequestBody RegistroObstetraRequest request) {
        try {
            adminService.registrarObstetra(request);
            return ResponseEntity.ok("Obstetra registrado correctamente");
        } catch (Exception e) {
            // Devolvemos badRequest si el email ya existe o falla algo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/obstetras/{id}")
    public ResponseEntity<String> actualizarObstetra(@PathVariable Long id, @RequestBody RegistroObstetraRequest request) {
        try {
            adminService.actualizarObstetra(id, request);
            return ResponseEntity.ok("Obstetra actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PatchMapping("/obstetras/{id}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long id) {
        try {
            adminService.cambiarEstadoObstetra(id);
            return ResponseEntity.ok("Estado del obstetra actualizado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pacientes")
    public ResponseEntity<List<PacienteCardDTO>> listarPacientes() {
        return ResponseEntity.ok(adminService.listarPacientes());
    }

    // CREAR
    @PostMapping("/pacientes")
    public ResponseEntity<String> crearPaciente(@RequestBody RegistroPacienteRequest request) {
        try {
            adminService.registrarPaciente(request);
            return ResponseEntity.ok("Paciente registrado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // (Opcional) ELIMINAR / CAMBIAR ESTADO
    @PatchMapping("/pacientes/{id}/estado")
    public ResponseEntity<String> cambiarEstadoPaciente(@PathVariable Long id) {
        try {
            adminService.cambiarEstadoPaciente(id);
            return ResponseEntity.ok("Estado actualizado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/pacientes/{id}")
    public ResponseEntity<String> actualizarPaciente(
            @PathVariable Long id, 
            @RequestBody RegistroPacienteRequest request
    ) {
        try {
            adminService.actualizarPaciente(id, request);
            return ResponseEntity.ok("Paciente actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   
}