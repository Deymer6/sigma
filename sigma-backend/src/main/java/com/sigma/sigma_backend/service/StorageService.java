package com.sigma.sigma_backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    // Nombre de la carpeta en la raíz del proyecto
    private final Path rootLocation = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            // Crea la carpeta 'uploads' si no existe al iniciar la app
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de uploads", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Error: El archivo está vacío.");
            }

            // Generar nombre único para el archivo
            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            
            // Copiar el archivo a la carpeta destino
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Retornar la URL pública para acceder al archivo
            // Genera: http://localhost:8080/uploads/nombre-unico.pdf
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(filename)
                    .toUriString();
            
            return fileUrl;

        } catch (IOException e) {
            throw new RuntimeException("Fallo al guardar el archivo.", e);
        }
    }
}