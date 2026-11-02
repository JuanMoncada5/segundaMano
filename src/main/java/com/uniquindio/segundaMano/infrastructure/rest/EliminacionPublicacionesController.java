package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.usecase.EliminarPublicacionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Eliminación lógica de una Publicación. Quién elimina sale del TOKEN (jwt.getSubject() = correo),
 * no del body: así nadie puede eliminar a nombre de otro.
 * 204 si sale bien, 404 si no existe, 422 si no es el dueño (regla del dominio).
 */
@RestController
@RequestMapping("/api/publicaciones")
public class EliminacionPublicacionesController {

    private final EliminarPublicacionUseCase eliminarPublicacion;

    public EliminacionPublicacionesController(EliminarPublicacionUseCase eliminarPublicacion) {
        this.eliminarPublicacion = eliminarPublicacion;
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable("codigo") String codigo,
                                         @AuthenticationPrincipal Jwt jwt) {
        eliminarPublicacion.ejecutar(codigo, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}