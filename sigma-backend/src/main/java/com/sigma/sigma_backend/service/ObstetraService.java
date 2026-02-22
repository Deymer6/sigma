package com.sigma.sigma_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.sigma_backend.dto.DashboardStaffDTO;
import com.sigma.sigma_backend.dto.DetalleAtencionDTO;
import com.sigma.sigma_backend.dto.FinalizarAtencionRequest;
import com.sigma.sigma_backend.dto.ObstetraDTO;
import com.sigma.sigma_backend.dto.PacienteCardDTO;
import com.sigma.sigma_backend.model.AnalisisClinico;
import com.sigma.sigma_backend.model.CitaMedica;
import com.sigma.sigma_backend.model.HistorialClinico;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.model.Paciente;
import com.sigma.sigma_backend.repository.*;
import org.springframework.transaction.annotation.Transactional;




import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Service
public class ObstetraService {

    private final ObstetraRepository obstetraRepository;
    private final CitaMedicaRepository citaRepository;

    private final HistorialClinicoRepository historialRepository;
    private final AnalisisClinicoRepository analisisRepository;
    private final StorageService storageService;
    private final PacienteRepository pacienteRepository;

    public ObstetraService(
            ObstetraRepository obstetraRepository, 
            CitaMedicaRepository citaRepository,
            HistorialClinicoRepository historialRepository, 
            AnalisisClinicoRepository analisisRepository,   
            StorageService storageService,
            PacienteRepository pacienteRepository                 
    ) {
        this.obstetraRepository = obstetraRepository;
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.analisisRepository = analisisRepository;
        this.storageService = storageService;
        this.pacienteRepository = pacienteRepository;
    }

    public DashboardStaffDTO obtenerDashboard(String emailUsuario,String fechaString) {
        // 1. Obtener datos del Obstetra logueado
        Obstetra obstetra = obstetraRepository.findByUsuarioEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("No se encontró el perfil"));
        

        LocalDate fechaSeleccionada;
        if (fechaString != null && !fechaString.isEmpty()) {
            // Si el front envía fecha, usamos esa
            fechaSeleccionada = LocalDate.parse(fechaString);
        } else {
            // Si no, usamos HOY
            fechaSeleccionada = LocalDate.now();
        }
        // Definir rango
        LocalDateTime inicioDia = fechaSeleccionada.atStartOfDay();
        LocalDateTime finDia = fechaSeleccionada.atTime(LocalTime.MAX); 

        // Usar TU método del repositorio
        List<CitaMedica> citasHoy = citaRepository.findCitasPorRango(
                obstetra.getIdObstetra(), 
                inicioDia, 
                finDia
        );
        DashboardStaffDTO dashboard = new DashboardStaffDTO();
        dashboard.setIdObstetra(obstetra.getIdObstetra());
        dashboard.setNombreCompleto(obstetra.getNombre() + " " + obstetra.getApellido());
        dashboard.setEspecialidad(obstetra.getEspecialidad());
        
        
        dashboard.setCantidadCitasHoy(citasHoy.size());
        dashboard.setCantidadPacientesMes(0); 

        // Convertir la lista de Entidades a DTOs para la tabla visual
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); 

        List<DashboardStaffDTO.CitaDiaDTO> listaCitasDTO = citasHoy.stream()
            .map(cita -> {
                DashboardStaffDTO.CitaDiaDTO dto = new DashboardStaffDTO.CitaDiaDTO();
                dto.setIdCita(cita.getIdCita());
                
                dto.setHora(cita.getFechaCita().format(timeFormatter)); 
                if(cita.getPaciente() != null) {
                    dto.setPacienteNombre(cita.getPaciente().getNombre() + " " + cita.getPaciente().getApellido());
                } else {
                    dto.setPacienteNombre("Paciente Desconocido");
                }
                
                dto.setMotivo(cita.getMotivoConsulta());
                dto.setEstado(cita.getEstadoCita()); 
                return dto;
            })
            .collect(Collectors.toList());

        dashboard.setCitasDeHoy(listaCitasDTO);

        return dashboard;
    }

    // Método para listar todos (Para el select del paciente)
    public List<ObstetraDTO> listarTodoso() {
        List<Obstetra> obstetras = obstetraRepository.findAll();
        
        return obstetras.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Mapper interno
    private ObstetraDTO toDTO(Obstetra obstetra) {
        return ObstetraDTO.builder()
                .id(obstetra.getIdObstetra())
                .nombre(obstetra.getNombre())
                .apellido(obstetra.getApellido())
                .especialidad(obstetra.getEspecialidad())
                .numColegiatura(obstetra.getNumColegiatura())
                .build();
    }

    public DetalleAtencionDTO obtenerDetalleAtencion(Long idCita) {
    CitaMedica cita = citaRepository.findById(idCita)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
    
    var paciente = cita.getPaciente();
    
    DetalleAtencionDTO dto = new DetalleAtencionDTO();
    dto.setIdCita(cita.getIdCita());
    
    
    dto.setHoraCita(cita.getFechaCita().format(DateTimeFormatter.ofPattern("hh:mm a")));
    
    dto.setPacienteNombre(paciente.getNombre() + " " + paciente.getApellido());
    
    // Calcular Edad
    if (paciente.getFechaNacimiento() != null) {
        dto.setEdad(Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears());
    }
    
    // Calcular Semanas Embarazo
    if (paciente.getFechaProbableParto() != null) {
        long diasFaltantes = ChronoUnit.DAYS.between(LocalDate.now(), paciente.getFechaProbableParto());
        long diasEmbarazo = (40 * 7) - diasFaltantes;
        dto.setSemanasEmbarazo("Semana " + (diasEmbarazo / 7));
    } else {
        dto.setSemanasEmbarazo("No registrado");
    }

    // Datos Dummy 
    dto.setTipoSangre("O+"); 
    dto.setAlergias("Ninguna");
    List<CitaMedica> historial = citaRepository.findTop3ByPaciente_IdPacienteAndEstadoCitaOrderByFechaCitaDesc(
            paciente.getIdPaciente(), 
            "Realizada"
    );
    List<String> historialFormateado = historial.stream()
            // Filtramos para que NO muestre la cita que estamos atendiendo ahorita mismo
            .filter(c -> !c.getIdCita().equals(idCita))
            .map(c -> {
                String fecha = c.getFechaCita().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                return fecha + " - " + c.getMotivoConsulta();
            })
            .collect(Collectors.toList());

    dto.setUltimasConsultas(historialFormateado);

    return dto;
    }
    

    @Transactional // Para que si falla algo, no guarde nada a medias
    // 
    public void finalizarCita(String datosJson, MultipartFile archivo) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        FinalizarAtencionRequest request = mapper.readValue(datosJson, FinalizarAtencionRequest.class);

        CitaMedica cita = citaRepository.findById(request.getIdCita())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Cambiar estado
        cita.setEstadoCita("Realizada");
        citaRepository.save(cita);

        // Crear Historial
        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(cita.getPaciente());
        historial.setObstetra(cita.getObstetra());
        historial.setCita(cita);
        historial.setFechaAtencion(LocalDateTime.now());
        historial.setDiagnostico(request.getDiagnostico());
        
        // Guardar receta como texto
        String recetaTexto = "";
        if(request.getReceta() != null) {
             recetaTexto = request.getReceta().stream()
                .map(m -> m.getNombre() + ": " + m.getIndicaciones())
                .collect(Collectors.joining("; "));
        }
        historial.setPrescripciones(recetaTexto);
        
        HistorialClinico historialGuardado = historialRepository.save(historial);
        // subir archivo
        if (archivo != null && !archivo.isEmpty()) {
            String urlArchivo = storageService.store(archivo);
            
            AnalisisClinico analisis = new AnalisisClinico();
            analisis.setHistorial(historialGuardado);
            analisis.setArchivoAdjuntoUrl(urlArchivo);
            analisis.setFechaRealizacion(LocalDate.now());
            
            
            String nombreDoc = request.getNombreArchivo();
            if (nombreDoc == null || nombreDoc.trim().isEmpty()) {
                nombreDoc = "Adjunto Consulta";
            }
            analisis.setTipoAnalisis(nombreDoc);
            
            
            analisisRepository.save(analisis);
        }
    }


    public List<PacienteCardDTO> listarPacientes(String terminoBusqueda) {
        List<Paciente> pacientes;

        // LÓGICA DE FILTRO
        if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
            // Si el usuario escribió algo, buscamos específicamente
            pacientes = pacienteRepository.buscarPacientes(terminoBusqueda);
        } else {
            // Si no escribió nada, traemos SOLO LOS ÚLTIMOS 50 (Protección de carga)
            pacientes = pacienteRepository.findTop50ByOrderByIdPacienteDesc();
        }

        // Mapear a DTO con DATOS REALES
        return pacientes.stream().map(p -> {
            PacienteCardDTO dto = new PacienteCardDTO();
            dto.setIdPaciente(p.getIdPaciente());
            dto.setNombreCompleto(p.getNombre() + " " + p.getApellido());
            dto.setDni(p.getDocumentoIdentificacion());

            // --- BUSCAR FECHA REAL DE ÚLTIMA VISITA ---
            // Buscamos la última cita 'Realizada' de este paciente
            List<CitaMedica> historial = citaRepository.findTop3ByPaciente_IdPacienteAndEstadoCitaOrderByFechaCitaDesc(
                    p.getIdPaciente(), "Realizada"
            );

            if (!historial.isEmpty()) {
                // Si tiene historial, tomamos la primera (la más reciente)
                dto.setUltimaVisita(historial.get(0).getFechaCita().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            } else {
                dto.setUltimaVisita("Sin visitas previas");
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ObstetraDTO> listarTodos() {
        List<Obstetra> obstetras = obstetraRepository.findAll();
        
        return obstetras.stream()
                .map(obstetra -> {
                    // Obtenemos datos del usuario vinculado (si existe)
                    String email = "";
                    String estado = "Desconocido";
                    
                    if (obstetra.getUsuario() != null) {
                        email = obstetra.getUsuario().getEmail();
                        estado = obstetra.getUsuario().getEstadoUsuario(); // "Activo"
                    }

                    return ObstetraDTO.builder()
                            .id(obstetra.getIdObstetra())
                            .nombre(obstetra.getNombre())
                            .apellido(obstetra.getApellido())
                            .especialidad(obstetra.getEspecialidad())
                            .numColegiatura(obstetra.getNumColegiatura())
                            // Mapeamos los nuevos campos
                            .email(email)
                            .estado(estado)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerHistorialCompleto(Long idPaciente) {
        // Buscamos datos del paciente para el título
        Paciente p = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Buscamos el historial ORDENADO (usando el nuevo método del repo)
        List<HistorialClinico> historial = historialRepository.findByPaciente_IdPacienteOrderByFechaAtencionDesc(idPaciente);

        List<Map<String, Object>> items = historial.stream().map(h -> {
            Map<String, Object> item = new HashMap<>();
            
            
            item.put("fecha", h.getFechaAtencion());
            item.put("motivo", h.getCita() != null ? h.getCita().getMotivoConsulta() : "Consulta General");
            item.put("diagnostico", h.getDiagnostico());
            item.put("doctor", "Dr. " + h.getObstetra().getNombre() + " " + h.getObstetra().getApellido());

            
            List<AnalisisClinico> analisis = analisisRepository.findByHistorial_IdHistorial(h.getIdHistorial());
            
            if (!analisis.isEmpty()) {
                // Tomamos la URL del primer análisis encontrado
                String urlCompleta = analisis.get(0).getArchivoAdjuntoUrl();
                item.put("archivosUrl", urlCompleta);
            }
            // -------------------------------------

            return item;
        }).collect(Collectors.toList());
        // 4. Empaquetamos todo
        Map<String, Object> response = new HashMap<>();
        response.put("paciente", p.getNombre() + " " + p.getApellido());
        response.put("items", items);
        
        return response;
    }
}