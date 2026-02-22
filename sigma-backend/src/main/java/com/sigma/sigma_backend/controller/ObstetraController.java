package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.DashboardStaffDTO;
import com.sigma.sigma_backend.dto.DetalleAtencionDTO;
import com.sigma.sigma_backend.dto.ObstetraDTO;
import com.sigma.sigma_backend.dto.PacienteCardDTO;
import com.sigma.sigma_backend.service.ObstetraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/staff")
public class ObstetraController {

    private final ObstetraService obstetraService;

    public ObstetraController(ObstetraService obstetraService) {
        this.obstetraService = obstetraService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStaffDTO> getDashboard(
        Authentication authentication,
        @RequestParam(required = false) String fecha
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(obstetraService.obtenerDashboard(email, fecha));
    }
    @GetMapping("/atencion/{idCita}")
    public ResponseEntity<DetalleAtencionDTO> getDetalleAtencion(@PathVariable Long idCita) {
        return ResponseEntity.ok(obstetraService.obtenerDetalleAtencion(idCita));
    }
    @PostMapping(value = "/atencion/finalizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> finalizarAtencion(
            @RequestParam("datos") String datosJson,  // El JSON viene como string
            @RequestParam(value = "archivo", required = false) MultipartFile archivo // Archivo opcional
    ) {
        try {
            obstetraService.finalizarCita(datosJson, archivo);
            return ResponseEntity.ok("Cita finalizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al procesar datos: " + e.getMessage());
        }
    }

    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<Map<String, Object>> getHistorialPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(obstetraService.obtenerHistorialCompleto(idPaciente));
    }


    @GetMapping("/pacientes")
    public ResponseEntity<List<PacienteCardDTO>> listarPacientes(
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(obstetraService.listarPacientes(query));
    }
    @GetMapping("/lista-simple") 
    public ResponseEntity<List<ObstetraDTO>> listarObstetrasSimple() {
        return ResponseEntity.ok(obstetraService.listarTodos());
    }
}