package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.usecase.ExpirarPublicacionesUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/** Rutas de administración: solo el rol ADMIN entra (ver SecurityConfig). */
@RestController
@RequestMapping("/api/admin/publicaciones")
public class AdminPublicacionController {

    private final ExpirarPublicacionesUseCase expirarPublicaciones;

    public AdminPublicacionController(ExpirarPublicacionesUseCase expirarPublicaciones) {
        this.expirarPublicaciones = expirarPublicaciones;
    }

    /** Expira las Publicaciones con 60 días o más sin actividad y responde cuántas expiraron. */
    @PostMapping("/expirar")
    public Map<String, Integer> expirar() {
        return Map.of("expiradas", expirarPublicaciones.ejecutar(LocalDate.now()));
    }
}