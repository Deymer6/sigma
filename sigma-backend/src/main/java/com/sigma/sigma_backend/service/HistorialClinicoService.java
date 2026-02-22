package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.HistorialClinicoDTO;
import com.sigma.sigma_backend.model.CitaMedica;
import com.sigma.sigma_backend.model.HistorialClinico;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.model.Paciente;
import com.sigma.sigma_backend.repository.CitaMedicaRepository;
import com.sigma.sigma_backend.repository.HistorialClinicoRepository;
import com.sigma.sigma_backend.repository.ObstetraRepository;
import com.sigma.sigma_backend.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final ObstetraRepository obstetraRepository;
    private final CitaMedicaRepository citaRepository;

    public HistorialClinicoService(HistorialClinicoRepository historialRepository, PacienteRepository pacienteRepository, ObstetraRepository obstetraRepository, CitaMedicaRepository citaRepository) {
        this.historialRepository = historialRepository;
        this.pacienteRepository = pacienteRepository;
        this.obstetraRepository = obstetraRepository;
        this.citaRepository = citaRepository;
    }

    //creamos un historial clinico
    public HistorialClinicoDTO crearHistorial(HistorialClinicoDTO dto) {
        // 1. Busca las entidades relacionadas
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Obstetra obstetra = obstetraRepository.findById(dto.getObstetraId())
                .orElseThrow(() -> new RuntimeException("Obstetra no encontrado"));
        
        // La cita es opcional, así que la buscamos si el ID no es nulo
        CitaMedica cita = null;
        if (dto.getCitaId() != null) {
            cita = citaRepository.findById(dto.getCitaId())
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        }

        // 2. Construye la entidad
        HistorialClinico historial = HistorialClinico.builder()
                .paciente(paciente)
                .obstetra(obstetra)
                .cita(cita)
                .fechaAtencion(LocalDateTime.now()) 
                .diagnostico(dto.getDiagnostico())
                .prescripciones(dto.getPrescripciones())
                .build();

        // 3. Guarda y convierte a DTO
        HistorialClinico historialGuardado = historialRepository.save(historial);
        return toDTO(historialGuardado);
    }

    // metodo para obtener el historial clinico por paciente
    public List<HistorialClinicoDTO> getHistorialPorPaciente(Long pacienteId) {
        return historialRepository.findByPaciente_IdPacienteOrderByFechaAtencionDesc(pacienteId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Método de utilidad para convertir a DTO
    private HistorialClinicoDTO toDTO(HistorialClinico historial) {
        return HistorialClinicoDTO.builder()
                .idHistorial(historial.getIdHistorial())
                .pacienteId(historial.getPaciente().getIdPaciente())
                .obstetraId(historial.getObstetra().getIdObstetra())
                .citaId(historial.getCita() != null ? historial.getCita().getIdCita() : null)
                .fechaAtencion(historial.getFechaAtencion())
                .diagnostico(historial.getDiagnostico())
                .prescripciones(historial.getPrescripciones())
                .obstetraNombre("Dr. " + historial.getObstetra().getNombre() + " " + historial.getObstetra().getApellido())
                .build();
    }

    



}