package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.AnalisisClinicoDTO;
import com.sigma.sigma_backend.model.AnalisisClinico;
import com.sigma.sigma_backend.model.HistorialClinico;
import com.sigma.sigma_backend.repository.AnalisisClinicoRepository;
import com.sigma.sigma_backend.repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalisisClinicoService {

    private final AnalisisClinicoRepository analisisRepository;
    private final HistorialClinicoRepository historialRepository;
    private final StorageService storageService; 

    public AnalisisClinicoService(
            AnalisisClinicoRepository analisisRepository,
            HistorialClinicoRepository historialRepository,
            StorageService storageService) {
        this.analisisRepository = analisisRepository;
        this.historialRepository = historialRepository;
        this.storageService = storageService;
    }

   
    public AnalisisClinicoDTO crearAnalisisConArchivo(
            Long idHistorial,
            String tipoAnalisis,
            String observaciones,
            LocalDate fecha,
            MultipartFile archivo 
    ) {
        // Validar Historial
        HistorialClinico historial = historialRepository.findById(idHistorial)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado"));

        //  Guardar el Archivo en Disco y obtener la URL
        String urlArchivo = null;
        if (archivo != null && !archivo.isEmpty()) {
            urlArchivo = storageService.store(archivo); 
        }

        // Crear y Guardar la Entidad en BD
        AnalisisClinico analisis = AnalisisClinico.builder()
                .historial(historial)
                .tipoAnalisis(tipoAnalisis)
                .valoresObservaciones(observaciones)
                .fechaRealizacion(fecha)
                .archivoAdjuntoUrl(urlArchivo) 
                .build();

        AnalisisClinico guardado = analisisRepository.save(analisis);
        return toDTO(guardado);
    }

    // metodo para obtener los analisis por historial clinico
    public List<AnalisisClinicoDTO> getAnalisisPorHistorial(Long historialId) {
        return analisisRepository.findByHistorialIdHistorial(historialId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Método de utilidad para convertir a DTO
    private AnalisisClinicoDTO toDTO(AnalisisClinico analisis) {
        return AnalisisClinicoDTO.builder()
                .idAnalisis(analisis.getIdAnalisis())
                .historialId(analisis.getHistorial().getIdHistorial())
                .tipoAnalisis(analisis.getTipoAnalisis())
                .fechaRealizacion(analisis.getFechaRealizacion())
                .valoresObservaciones(analisis.getValoresObservaciones())
                .archivoAdjuntoUrl(analisis.getArchivoAdjuntoUrl())
                .build();
    }
    // metodo para eliminar analisis
    public void eliminarAnalisis(Long idAnalisis) {
        
        if (!analisisRepository.existsById(idAnalisis)) {
            throw new RuntimeException("No se encontró el análisis clínico con ID: " + idAnalisis);
        }
        
        
        analisisRepository.deleteById(idAnalisis);
    }
    // Obtener todos los analisis del paciente 
    public List<AnalisisClinicoDTO> getAnalisisPorPaciente(Long pacienteId) {
        return analisisRepository.findAnalisisPorPacienteId(pacienteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}