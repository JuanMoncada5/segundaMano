package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.CalificarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.application.usecase.CalificarUniCambistaUseCase;
import com.uniquindio.segundaMano.infrastructure.rest.dto.CalificacionBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Funcionalidad propia: calificar al otro participante de una Transacción completada.
 * Quién califica sale del token (Principal.getName() = correo).
 * 200 con el UniCambista calificado; 400 si las estrellas no son de 1 a 5;
 * 404 si la Transacción no existe; 422 si no está completada, no participó o ya calificó.
 */
@RestController
@RequestMapping("/api/transacciones")
public class CalificacionController {

    private final CalificarUniCambistaUseCase calificarUniCambista;

    public CalificacionController(CalificarUniCambistaUseCase calificarUniCambista) {
        this.calificarUniCambista = calificarUniCambista;
    }

    @PostMapping("/{id}/calificacion")
    public UniCambistaResponse calificar(@PathVariable("id") String id,
                                         @Valid @RequestBody CalificacionBody body,
                                         Principal usuario) {
        return calificarUniCambista.ejecutar(
                new CalificarUniCambistaRequest(id, usuario.getName(), body.estrellas()));
    }
}