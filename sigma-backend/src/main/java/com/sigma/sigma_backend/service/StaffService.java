package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.DashboardStaffDTO;
import com.sigma.sigma_backend.model.CitaMedica;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.repository.CitaMedicaRepository;
import com.sigma.sigma_backend.repository.ObstetraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final ObstetraRepository obstetraRepository;
    private final CitaMedicaRepository citaRepository;

    public StaffService(ObstetraRepository obstetraRepository, CitaMedicaRepository citaRepository) {
        this.obstetraRepository = obstetraRepository;
        this.citaRepository = citaRepository;
    }

    public DashboardStaffDTO obtenerDashboard(String email) {
        //Identificar al Obstetra
        Obstetra obstetra = obstetraRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Obstetra no encontrado con email: " + email));

        DashboardStaffDTO dto = new DashboardStaffDTO();
        dto.setIdObstetra(obstetra.getIdObstetra());
        dto.setNombreCompleto(obstetra.getNombre() + " " + obstetra.getApellido());
        dto.setEspecialidad(obstetra.getEspecialidad());

        //  Definir el rango de "HOY" (00:00 a 23:59)
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(LocalTime.MAX);

        // Buscar citas
        List<CitaMedica> citasHoy = citaRepository.findCitasPorRango(obstetra.getIdObstetra(), inicioDia, finDia);

        dto.setCantidadCitasHoy(citasHoy.size());
        // dto.setCantidadPacientesMes(...); 

        // 4. Convertir lista de citas
        List<DashboardStaffDTO.CitaDiaDTO> citasDto = citasHoy.stream().map(c -> {
            DashboardStaffDTO.CitaDiaDTO cd = new DashboardStaffDTO.CitaDiaDTO();
            cd.setIdCita(c.getIdCita());
            cd.setPacienteNombre(c.getPaciente().getNombre() + " " + c.getPaciente().getApellido());
            cd.setMotivo(c.getMotivoConsulta());
            cd.setEstado(c.getEstadoCita());
            
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            cd.setHora(c.getFechaCita().format(formatter));
            
            return cd;
        }).collect(Collectors.toList());

        dto.setCitasDeHoy(citasDto);

        return dto;
    }
}