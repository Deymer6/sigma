package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.CitaMedicaDTO;
import com.sigma.sigma_backend.model.CitaMedica;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.model.Paciente;
import com.sigma.sigma_backend.repository.CitaMedicaRepository;
import com.sigma.sigma_backend.repository.ObstetraRepository;
import com.sigma.sigma_backend.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaMedicaService {

    private final CitaMedicaRepository citaMedicaRepository;
    private final PacienteRepository pacienteRepository;
    private final ObstetraRepository obstetraRepository;

    public CitaMedicaService(CitaMedicaRepository citaMedicaRepository, PacienteRepository pacienteRepository, ObstetraRepository obstetraRepository) {
        this.citaMedicaRepository = citaMedicaRepository;
        this.pacienteRepository = pacienteRepository;
        this.obstetraRepository = obstetraRepository;
    }

    // Metodo para crear cita
    public CitaMedicaDTO crearCita(CitaMedicaDTO dto) {
        
        // --- 1. VALIDACIÓN DE REGLA DE NEGOCIO (Máximo 2 Citas Activas) ---
        long citasActivas = citaMedicaRepository.countByPacienteIdPacienteAndEstadoCita(
                dto.getPacienteId(), 
                "Programada"
        );

        if (citasActivas >= 2) {
            // Lanzamos una excepción para que el controlador devuelva error
            throw new RuntimeException("Has alcanzado el límite de 2 citas programadas. Debes completar o cancelar una para agendar otra.");
        }

        // ---Si pasa la validación, continuamos con la lógica normal ---
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Obstetra obstetra = obstetraRepository.findById(dto.getObstetraId())
                .orElseThrow(() -> new RuntimeException("Obstetra no encontrado"));

        CitaMedica nuevaCita = CitaMedica.builder()
                .paciente(paciente)
                .obstetra(obstetra)
                .fechaCita(dto.getFechaCita())
                .motivoConsulta(dto.getMotivoConsulta())
                .estadoCita("Programada")
                .build();

        CitaMedica citaGuardada = citaMedicaRepository.save(nuevaCita);
        return toDTO(citaGuardada);
    }

    // Método para obtener citas por paciente
    public List<CitaMedicaDTO> getCitasPorPaciente(Long pacienteId) {
        return citaMedicaRepository.findByPacienteId(pacienteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Método de utilidad para convertir Entidad a DTO
    private CitaMedicaDTO toDTO(CitaMedica cita) {
        return CitaMedicaDTO.builder()
                .idCita(cita.getIdCita())
                .pacienteId(cita.getPaciente().getIdPaciente())
                .obstetraId(cita.getObstetra().getIdObstetra())
                .pacienteNombre(cita.getPaciente().getNombre() + " " + cita.getPaciente().getApellido())
                .obstetraNombre(cita.getObstetra().getNombre() + " " + cita.getObstetra().getApellido())
                .fechaCita(cita.getFechaCita())
                .motivoConsulta(cita.getMotivoConsulta())
                .estadoCita(cita.getEstadoCita())
                .build();
    }
    // Metodo para cancelar cita
    public CitaMedicaDTO cancelarCita(Long idCita) {
        CitaMedica cita = citaMedicaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        //no cancelar citas pasadas)
        if (cita.getFechaCita().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("No se pueden cancelar citas pasadas");
        }

        cita.setEstadoCita("Cancelada"); 
        
        CitaMedica citaGuardada = citaMedicaRepository.save(cita);
        return toDTO(citaGuardada);
    }



}