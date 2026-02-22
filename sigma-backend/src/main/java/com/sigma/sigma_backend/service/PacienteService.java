package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.DashboardPacienteDTO;
import com.sigma.sigma_backend.model.*;
import com.sigma.sigma_backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaMedicaRepository citaMedicaRepository;
    private final HistorialClinicoRepository historialClinicoRepository;
    private final AnalisisClinicoRepository analisisClinicoRepository;

    public PacienteService(
            PacienteRepository pacienteRepository,
            CitaMedicaRepository citaMedicaRepository,
            HistorialClinicoRepository historialClinicoRepository,
            AnalisisClinicoRepository analisisClinicoRepository
    ) {
        this.pacienteRepository = pacienteRepository;
        this.citaMedicaRepository = citaMedicaRepository;
        this.historialClinicoRepository = historialClinicoRepository;
        this.analisisClinicoRepository = analisisClinicoRepository;
    }

    public DashboardPacienteDTO obtenerDatosDashboard(String email) {
        // 1. Buscar Paciente
        Paciente paciente = pacienteRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con email: " + email));

        DashboardPacienteDTO dto = new DashboardPacienteDTO();
        dto.setIdPaciente(paciente.getIdPaciente());
        dto.setPacienteNombre(paciente.getNombre() + " " + paciente.getApellido());

        // 2. EMBARAZO
        if (paciente.getFechaProbableParto() != null) {
            DashboardPacienteDTO.EmbarazoResumenDTO embarazoDto = new DashboardPacienteDTO.EmbarazoResumenDTO();
            
            // Usamos el helper por si la fecha viene en formato antiguo o nuevo
            LocalDate fechaParto = convertirALocalDate(paciente.getFechaProbableParto());
            embarazoDto.setFechaProbableParto(fechaParto);
            
            LocalDate hoy = LocalDate.now();
            LocalDate fechaConcepcion = fechaParto.minusDays(280);
            
            long semanas = ChronoUnit.WEEKS.between(fechaConcepcion, hoy);
            long diasFaltantes = ChronoUnit.DAYS.between(hoy, fechaParto);
            
            embarazoDto.setSemanas((int) Math.max(0, semanas));
            embarazoDto.setDiasFaltantes(Math.max(0, diasFaltantes));
            
            if (semanas < 13) embarazoDto.setTrimestre("Primer Trimestre");
            else if (semanas < 27) embarazoDto.setTrimestre("Segundo Trimestre");
            else embarazoDto.setTrimestre("Tercer Trimestre");

            int porcentaje = (int) ((semanas / 40.0) * 100);
            embarazoDto.setPorcentajeProgreso(Math.min(100, Math.max(0, porcentaje)));
            
            dto.setEmbarazo(embarazoDto);
        }

        // 3. PRÓXIMA CITA
        List<CitaMedica> citas = citaMedicaRepository.findByPacienteId(paciente.getIdPaciente());
        
        Optional<CitaMedica> proximaCitaOpt = citas.stream()
                .filter(c -> {
                    LocalDateTime fechaHoraCita = c.getFechaCita(); 
                    return c.getEstadoCita().equals("Programada") && 
                           (fechaHoraCita.isAfter(LocalDateTime.now()) || 
                            fechaHoraCita.toLocalDate().isEqual(LocalDate.now()));
                })
                .min(Comparator.comparing(CitaMedica::getFechaCita));

        // Inicializar listas para evitar nulos
        dto.setUltimosResultados(new ArrayList<>());
        dto.setIndicaciones(new ArrayList<>());

        if (proximaCitaOpt.isPresent()) {
            CitaMedica cita = proximaCitaOpt.get();
            
            DashboardPacienteDTO.CitaResumenDTO citaDto = new DashboardPacienteDTO.CitaResumenDTO();
            citaDto.setFecha(cita.getFechaCita().toLocalDate());
            // Formatear hora (10:00 AM)
            citaDto.setHora(cita.getFechaCita().format(DateTimeFormatter.ofPattern("hh:mm a"))); 
            
            if (cita.getObstetra() != null) {
                citaDto.setDoctorNombre("Dr. " + cita.getObstetra().getNombre() + " " + cita.getObstetra().getApellido());
                citaDto.setEspecialidad(cita.getObstetra().getEspecialidad());
            }
            dto.setProximaCita(citaDto);
            
            // 4. MI OBSTETRA (Basado en la próxima cita)
            if (cita.getObstetra() != null) {
                Obstetra obstetra = cita.getObstetra();
                DashboardPacienteDTO.ObstetraResumenDTO obsDto = new DashboardPacienteDTO.ObstetraResumenDTO();
                obsDto.setNombre("Dr. " + obstetra.getNombre() + " " + obstetra.getApellido());
                obsDto.setEspecialidad(obstetra.getEspecialidad());
                obsDto.setTelefono(obstetra.getTelefonoContacto());
                obsDto.setWhatsapp(obstetra.getTelefonoContacto());
                dto.setObstetra(obsDto);
            }
        }

        // 5. RESULTADOS (Análisis Clínicos)
        List<AnalisisClinico> analisisRecientes = analisisClinicoRepository.findTop3ByHistorial_Paciente_IdPacienteOrderByFechaRealizacionDesc(paciente.getIdPaciente());
        
        List<DashboardPacienteDTO.ResultadoResumenDTO> resultadosDto = analisisRecientes.stream().map(a -> {
            DashboardPacienteDTO.ResultadoResumenDTO r = new DashboardPacienteDTO.ResultadoResumenDTO();
            
            r.setNombre(a.getTipoAnalisis() != null ? a.getTipoAnalisis() : "Documento Médico");
            r.setFecha(a.getFechaRealizacion()); 
            r.setArchivoUrl(a.getArchivoAdjuntoUrl());
            
            String url = a.getArchivoAdjuntoUrl().toLowerCase();
            if (url.endsWith(".pdf")) r.setTipo("pdf");
            else if (url.endsWith(".jpg") || url.endsWith(".png")) r.setTipo("img");
            else r.setTipo("file");
            
            return r;
        }).collect(Collectors.toList());
        
        dto.setUltimosResultados(resultadosDto);

        // 6. INDICACIONES (De la última visita en historial)
        List<HistorialClinico> historiales = historialClinicoRepository.findByPaciente_IdPacienteOrderByFechaAtencionDesc(paciente.getIdPaciente());
        
        if (!historiales.isEmpty()) {
            HistorialClinico ultimo = historiales.get(0);
            if (ultimo.getPrescripciones() != null && !ultimo.getPrescripciones().isEmpty()) {
                DashboardPacienteDTO.IndicacionResumenDTO ind = new DashboardPacienteDTO.IndicacionResumenDTO();
                ind.setMedicamento(ultimo.getPrescripciones());
                ind.setDosis("Ver detalle");
                dto.getIndicaciones().add(ind);
            }
        }

        return dto;
    }

    // Método auxiliar (Debe estar DENTRO de la clase, pero FUERA de obtenerDatosDashboard)
    private LocalDate convertirALocalDate(Object fecha) {
        if (fecha == null) return null;
        
        if (fecha instanceof java.sql.Date) {
            return ((java.sql.Date) fecha).toLocalDate();
        }
        if (fecha instanceof java.util.Date) {
            return ((java.util.Date) fecha).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (fecha instanceof LocalDate) {
            return (LocalDate) fecha;
        }
        return LocalDate.now();
    }
}