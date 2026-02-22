package com.sigma.sigma_backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura nuestras excepciones de lógica de negocio (DNI repetido, etc.)
     * y las convierte en un 400 Bad Request con un mensaje claro.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Creamos un JSON simple: { "message": "El error..." }
        Map<String, String> errorResponse = Map.of("message", ex.getMessage());
        
        // Devolvemos un 400 (Bad Request) con el mensaje de error
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}