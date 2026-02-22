package com.sigma.sigma_backend.controller;

import com.sigma.sigma_backend.dto.AuthResponse;
import com.sigma.sigma_backend.dto.CreateObstetraRequest;
import com.sigma.sigma_backend.dto.LoginRequest;
import com.sigma.sigma_backend.dto.RegisterRequest;
import com.sigma.sigma_backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/crear-obstetra")
    @PreAuthorize("hasAuthority('ROL_ADMINISTRATIVO')")
    public ResponseEntity<?> createObstetra(
            @RequestBody CreateObstetraRequest request
    ) {
        authService.createObstetra(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}