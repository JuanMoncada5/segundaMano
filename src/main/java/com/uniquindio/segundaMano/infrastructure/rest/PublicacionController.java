package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.OfertaResponse;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.application.usecase.AceptarOfertaUseCase;
import com.uniquindio.segundaMano.application.usecase.OfertarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.PublicarArticuloUseCase;
import com.uniquindio.segundaMano.infrastructure.rest.dto.AceptarOfertaBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.OfertarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.PublicarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.PublicacionRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/** Operaciones que CAMBIAN cosas sobre las Publicaciones (las consultas están en ConsultaPublicacionesController). */
@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    private final PublicarArticuloUseCase publicarArticulo;
    private final OfertarArticuloUseCase ofertarArticulo;
    private final AceptarOfertaUseCase aceptarOferta;
    private final PublicacionRestMapper mapper;

    public PublicacionController(PublicarArticuloUseCase publicarArticulo,
                                 OfertarArticuloUseCase ofertarArticulo,
                                 AceptarOfertaUseCase aceptarOferta,
                                 PublicacionRestMapper mapper) {
        this.publicarArticulo = publicarArticulo;
        this.ofertarArticulo = ofertarArticulo;
        this.aceptarOferta = aceptarOferta;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PublicacionResponse> publicar(@Valid @RequestBody PublicarArticuloBody body) {
        PublicacionResponse creada = publicarArticulo.ejecutar(mapper.toRequest(body));
        return ResponseEntity.created(URI.create("/api/publicaciones/" + creada.codigo())).body(creada);
    }

    @PostMapping("/{codigo}/ofertas")
    public ResponseEntity<OfertaResponse> ofertar(@PathVariable("codigo") String codigo,
                                                  @Valid @RequestBody OfertarArticuloBody body) {
        OfertaResponse creada = ofertarArticulo.ejecutar(mapper.toRequest(codigo, body));
        return ResponseEntity.created(URI.create("/api/publicaciones/" + codigo + "/ofertas/" + creada.id())).body(creada);
    }

    /**
     * Endpoint semántico (método del dominio: aceptarOferta). Aceptar inicia la Transacción,
     * por eso responde con la Transacción creada y su Location.
     */
    @PutMapping("/{codigo}/ofertas/{idOferta}/aceptar")
    public ResponseEntity<TransaccionResponse> aceptarOferta(@PathVariable("codigo") String codigo,
                                                             @PathVariable("idOferta") String idOferta,
                                                             @Valid @RequestBody AceptarOfertaBody body) {
        TransaccionResponse transaccion = aceptarOferta.ejecutar(mapper.toRequest(codigo, idOferta, body));
        return ResponseEntity.ok()
                .header("Location", "/api/transacciones/" + transaccion.id())
                .body(transaccion);
    }
}
