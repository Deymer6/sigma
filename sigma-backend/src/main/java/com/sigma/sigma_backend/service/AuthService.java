package com.sigma.sigma_backend.service;

import com.sigma.sigma_backend.dto.AuthResponse;
import com.sigma.sigma_backend.dto.CreateObstetraRequest;
import com.sigma.sigma_backend.dto.LoginRequest;
import com.sigma.sigma_backend.dto.RegisterRequest;
import com.sigma.sigma_backend.model.Obstetra;
import com.sigma.sigma_backend.model.Paciente;
import com.sigma.sigma_backend.model.Role;
import com.sigma.sigma_backend.model.Usuario;
import com.sigma.sigma_backend.repository.ObstetraRepository;
import com.sigma.sigma_backend.repository.PacienteRepository;
import com.sigma.sigma_backend.repository.RoleRepository;
import com.sigma.sigma_backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PacienteRepository pacienteRepository;
    private final ObstetraRepository obstetraRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PacienteRepository pacienteRepository, ObstetraRepository obstetraRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationProvider authenticationProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.pacienteRepository = pacienteRepository;
        this.obstetraRepository = obstetraRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationProvider = authenticationProvider;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso.");
        }
        if (pacienteRepository.findByDocumentoIdentificacion(request.getDocumentoIdentificacion()).isPresent()) {
            throw new RuntimeException("Error: El DNI ya está registrado.");
        }

        Role userRole = roleRepository.findByNombreRol("ROL_PACIENTE")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'ROL_PACIENTE' no encontrado."));

        var usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .estadoUsuario("Activo")
                .build();
        Usuario usuarioGuardado = userRepository.save(usuario);

        var paciente = Paciente.builder()
                .usuario(usuarioGuardado)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .documentoIdentificacion(request.getDocumentoIdentificacion())
                .fechaNacimiento(request.getFechaNacimiento())
                .estadoGestacional("Gestante")
                .build();
        pacienteRepository.save(paciente);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authorities", List.of(usuarioGuardado.getRole().getNombreRol()));
        
        String jwtToken = jwtService.generateToken(extraClaims, usuarioGuardado.getUsername());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authorities", List.of(usuario.getRole().getNombreRol()));

        String jwtToken = jwtService.generateToken(extraClaims, usuario.getUsername());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void createObstetra(CreateObstetraRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso.");
        }

        Role userRole = roleRepository.findByNombreRol("ROL_OBSTETRA")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'ROL_OBSTETRA' no encontrado."));

        var usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .estadoUsuario("Activo")
                .build();
        Usuario usuarioGuardado = userRepository.save(usuario);

        var obstetra = Obstetra.builder()
                .usuario(usuarioGuardado)
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .numColegiatura(request.getNumColegiatura())
                .especialidad(request.getEspecialidad())
                .build();
        obstetraRepository.save(obstetra);
    }
}