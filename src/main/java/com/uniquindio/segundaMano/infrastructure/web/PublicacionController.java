package com.uniquindio.segundaMano.infrastructure.web;

import com.uniquindio.segundaMano.application.dto.OfertaResponse;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.application.usecase.OfertarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.PublicarArticuloUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * El controller solo traduce HTTP <-> caso de uso. No tiene reglas de negocio
 * ni try/catch: los errores los convierte en códigos HTTP el ManejadorGlobalErrores.
 */
@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    /** Cuerpo de la petición para ofertar: el código de la Publicación viene en la URL. */
    public record NuevaOfertaRequest(String correoOferente, BigDecimal montoPropuesto) {
    }

    private final PublicarArticuloUseCase publicarArticulo;
    private final OfertarArticuloUseCase ofertarArticulo;

    public PublicacionController(PublicarArticuloUseCase publicarArticulo,
                                 OfertarArticuloUseCase ofertarArticulo) {
        this.publicarArticulo = publicarArticulo;
        this.ofertarArticulo = ofertarArticulo;
    }

    @PostMapping
    public ResponseEntity<PublicacionResponse> publicar(@RequestBody PublicarArticuloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicarArticulo.ejecutar(request));
    }

    @PostMapping("/{codigo}/ofertas")
    public ResponseEntity<OfertaResponse> ofertar(@PathVariable("codigo") String codigo,
                                                  @RequestBody NuevaOfertaRequest cuerpo) {
        OfertarArticuloRequest request =
                new OfertarArticuloRequest(codigo, cuerpo.correoOferente(), cuerpo.montoPropuesto());
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertarArticulo.ejecutar(request));
    }
}