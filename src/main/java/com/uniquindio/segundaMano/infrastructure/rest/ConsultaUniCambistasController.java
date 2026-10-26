package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.application.usecase.ConsultarUniCambistaUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Solo consultas: no cambian nada. */
@RestController
@RequestMapping("/api/unicambistas")
public class ConsultaUniCambistasController {

    private final ConsultarUniCambistaUseCase consultarUniCambista;

    public ConsultaUniCambistasController(ConsultarUniCambistaUseCase consultarUniCambista) {
        this.consultarUniCambista = consultarUniCambista;
    }

    @GetMapping("/{correo}")
    public UniCambistaResponse consultar(@PathVariable("correo") String correo) {
        return consultarUniCambista.ejecutar(correo);
    }
}
