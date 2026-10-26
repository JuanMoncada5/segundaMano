package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.VerificarCorreoUseCase;
import com.uniquindio.segundaMano.infrastructure.rest.dto.RegistrarUniCambistaBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.VerificarCorreoBody;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.UniCambistaRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/unicambistas")
public class UniCambistaController {

    private final RegistrarUniCambistaUseCase registrarUniCambista;
    private final VerificarCorreoUseCase verificarCorreo;
    private final UniCambistaRestMapper mapper;

    public UniCambistaController(RegistrarUniCambistaUseCase registrarUniCambista,
                                 VerificarCorreoUseCase verificarCorreo,
                                 UniCambistaRestMapper mapper) {
        this.registrarUniCambista = registrarUniCambista;
        this.verificarCorreo = verificarCorreo;
        this.mapper = mapper;
    }

    /** 201 Created + cabecera Location apuntando al recurso nuevo. */
    @PostMapping
    public ResponseEntity<UniCambistaResponse> registrar(@Valid @RequestBody RegistrarUniCambistaBody body) {
        UniCambistaResponse creado = registrarUniCambista.ejecutar(mapper.toRequest(body));
        return ResponseEntity.created(URI.create("/api/unicambistas/" + creado.correoInstitucional())).body(creado);
    }

    /** Endpoint semántico: se llama como el método del dominio (verificarCorreo). */
    @PutMapping("/verificar")
    public UniCambistaResponse verificarCorreo(@Valid @RequestBody VerificarCorreoBody body) {
        return verificarCorreo.ejecutar(body.correoInstitucional());
    }
}
