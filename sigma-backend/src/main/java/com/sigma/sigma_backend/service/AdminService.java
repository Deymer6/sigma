package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.DashboardAdminDTO;
import com.sigma.sigma_backend.dto.PacienteCardDTO;
import com.sigma.sigma_backend.dto.RegistroObstetraRequest;
import com.sigma.sigma_backend.dto.RegistroPacienteRequest;
import com.sigma.sigma_backend.model.*;
import com.sigma.sigma_backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    private final UserRepository usuarioRepository;
    private final ObstetraRepository obstetraRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaMedicaRepository citaRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository usuarioRepository,
                        ObstetraRepository obstetraRepository,
                        PacienteRepository pacienteRepository,
                        CitaMedicaRepository citaRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.obstetraRepository = obstetraRepository;
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DashboardAdminDTO obtenerDashboard() {
        DashboardAdminDTO dto = new DashboardAdminDTO();
        
        dto.setTotalObstetras(obstetraRepository.count());
        dto.setTotalPacientes(pacienteRepository.count());
        

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).with(java.time.LocalTime.MIN);
        dto.setCitasEsteMes(0L); 
        LocalDateTime finMes = ahora.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).with(java.time.LocalTime.MAX);
        long citasMes = citaRepository.countByFechaCitaBetween(inicioMes, finMes);

        dto.setCitasEsteMes(citasMes);
        
        /*Map<String, Long> grafico = new LinkedHashMap<>();
        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        for (String mes : nombresMeses) {
            grafico.put(mes, 0L);
        }

        // 2. Consultamos a la BD 
        int anioActual = LocalDate.now().getYear();
        List<Object[]> resultados = citaRepository.contarCitasPorMes(anioActual);

        
        for (Object[] fila : resultados) {
            // fila[0] es el mes (1 a 12)
            // fila[1] es la cantidad
            
            int mesNumero = ((Number) fila[0]).intValue(); 
            long cantidad = ((Number) fila[1]).longValue();

            
            if (mesNumero >= 1 && mesNumero <= 12) {
                String nombreMes = nombresMeses[mesNumero - 1];
                grafico.put(nombreMes, cantidad);
            }
        }
        
        dto.setCitasPorMes(grafico);*/




        Map<String, Long> grafico = new LinkedHashMap<>();
        grafico.put("Ene", 180L);
        grafico.put("Feb", 210L);
        grafico.put("Mar", 250L);
        grafico.put("Abr", 240L);
        grafico.put("May", 280L);
        grafico.put("Jun", 300L);
        grafico.put("Jul", 320L);
        grafico.put("Ago", 350L);
        grafico.put("Sep", 330L);
        grafico.put("Oct", 380L);
        grafico.put("Nov", 19L);
        dto.setCitasPorMes(grafico);

        dto.setActividadReciente(List.of(
            "Dr. Perez finalizó una cita",
            "Nuevo paciente registrado: Juan",
            "Cita agendada para mañana"
        ));

        return dto;
    }

    @Transactional
    public void registrarObstetra(RegistroObstetraRequest request) {
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); 
        usuario.setEstadoUsuario("Activo");
        usuario.setFechaCreacion(LocalDateTime.now());

        Role role = roleRepository.findByNombreRol("ROL_OBSTETRA")
                .orElseThrow(() -> new RuntimeException("Error: Rol de Obstetra no encontrado en BD"));
        
        usuario.setRole(role);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Obstetra obstetra = new Obstetra();
        obstetra.setUsuario(usuarioGuardado);
        obstetra.setNombre(request.getNombre());
        obstetra.setApellido(request.getApellido());
        obstetra.setNumColegiatura(request.getNumColegiatura());
        obstetra.setEspecialidad(request.getEspecialidad());
        
        obstetraRepository.save(obstetra);
    }

    @Transactional
    public void actualizarObstetra(Long id, RegistroObstetraRequest request) {
        Obstetra obstetra = obstetraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obstetra no encontrado"));
        
        // Actualizamos datos profesionales
        obstetra.setNombre(request.getNombre());
        obstetra.setApellido(request.getApellido());
        obstetra.setNumColegiatura(request.getNumColegiatura());
        obstetra.setEspecialidad(request.getEspecialidad());
        obstetra.setTelefonoContacto(request.getTelefono()); 

        
        if (!obstetra.getUsuario().getEmail().equals(request.getEmail())) {
             // Validar que el nuevo email no exista
             if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                 throw new RuntimeException("El nuevo email ya está en uso.");
             }
             obstetra.getUsuario().setEmail(request.getEmail());
             usuarioRepository.save(obstetra.getUsuario());
        }

        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            obstetra.getUsuario().setPassword(passwordEncoder.encode(request.getPassword()));
            usuarioRepository.save(obstetra.getUsuario());
        }

        obstetraRepository.save(obstetra);
    }

    @Transactional
    // cambiar el estado de una obstretra
    public void cambiarEstadoObstetra(Long id) {
        Obstetra obstetra = obstetraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obstetra no encontrado"));
        
        Usuario usuario = obstetra.getUsuario();
        
        
        if ("Activo".equals(usuario.getEstadoUsuario())) {
            usuario.setEstadoUsuario("Inactivo");
        } else {
            usuario.setEstadoUsuario("Activo");
        }
        
        usuarioRepository.save(usuario);
    }



    public List<PacienteCardDTO> listarPacientes() {
        return pacienteRepository.findTop50ByOrderByIdPacienteDesc().stream()
            .map(p -> {
                String estado = "Desconocido";
                String email = "";

                if (p.getUsuario() != null) {
                    estado = p.getUsuario().getEstadoUsuario();
                    email = p.getUsuario().getEmail();
                }

                return PacienteCardDTO.builder()
                        .idPaciente(p.getIdPaciente())
                        .nombreCompleto(p.getNombre() + " " + p.getApellido())
                        .dni(p.getDocumentoIdentificacion())
                        .fechaNacimiento(p.getFechaNacimiento())
                        .email(email)
                        .estado(estado)
                        .build();
            }).toList();
    }

    // 2. REGISTRAR PACIENTE
    @Transactional
    public void registrarPaciente(RegistroPacienteRequest request) {
        // Validar Email
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }

        // Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstadoUsuario("Activo");
        usuario.setFechaCreacion(LocalDateTime.now());

        Role rol = roleRepository.findByNombreRol("ROL_PACIENTE")
                .orElseThrow(() -> new RuntimeException("Rol PACIENTE no encontrado"));
        usuario.setRole(rol);

        Usuario guardado = usuarioRepository.save(usuario);

        // Crear Paciente
        Paciente paciente = new Paciente();
        paciente.setUsuario(guardado);
        paciente.setNombre(request.getNombre());
        paciente.setApellido(request.getApellido());
        paciente.setDocumentoIdentificacion(request.getDni());
        paciente.setFechaNacimiento(request.getFechaNacimiento()); // Fecha nac
        
        pacienteRepository.save(paciente);
    }

    // 3. CAMBIAR ESTADO PACIENTE
    @Transactional
    public void cambiarEstadoPaciente(Long id) {
        Paciente p = pacienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            
        Usuario u = p.getUsuario();
        
        if("Activo".equals(u.getEstadoUsuario())) {
            u.setEstadoUsuario("Inactivo");
        } else {
            u.setEstadoUsuario("Activo");
        }
        usuarioRepository.save(u);
    }
    
    // 4. ACTUALIZAR PACIENTE
    @Transactional
    public void actualizarPaciente(Long id, RegistroPacienteRequest request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.setNombre(request.getNombre());
        paciente.setApellido(request.getApellido());
        paciente.setDocumentoIdentificacion(request.getDni());
        
        if (request.getFechaNacimiento() != null) {
            paciente.setFechaNacimiento(request.getFechaNacimiento());
        }

        // Email
        if (!paciente.getUsuario().getEmail().equals(request.getEmail())) {
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email ocupado.");
            }
            paciente.getUsuario().setEmail(request.getEmail());
            usuarioRepository.save(paciente.getUsuario());
        }

        // Password
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            paciente.getUsuario().setPassword(passwordEncoder.encode(request.getPassword()));
            usuarioRepository.save(paciente.getUsuario());
        }

        pacienteRepository.save(paciente);
    }
}