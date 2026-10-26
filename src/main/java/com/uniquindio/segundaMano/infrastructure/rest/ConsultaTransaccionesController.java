package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.application.usecase.ConsultarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarTransaccionesDeUniCambistaUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Solo consultas: no cambian nada. */
@RestController
@RequestMapping("/api/transacciones")
public class ConsultaTransaccionesController {

    private final ConsultarTransaccionUseCase consultarTransaccion;
    private final ListarTransaccionesDeUniCambistaUseCase listarTransaccionesDeUniCambista;

    public ConsultaTransaccionesController(ConsultarTransaccionUseCase consultarTransaccion,
                                           ListarTransaccionesDeUniCambistaUseCase listarTransaccionesDeUniCambista) {
        this.consultarTransaccion = consultarTransaccion;
        this.listarTransaccionesDeUniCambista = listarTransaccionesDeUniCambista;
    }

    @GetMapping("/{id}")
    public TransaccionResponse consultar(@PathVariable("id") String id) {
        return consultarTransaccion.ejecutar(id);
    }

    /** GET /api/transacciones?correo=laura@uqvirtual.edu.co */
    @GetMapping
    public List<TransaccionResponse> listarDeUniCambista(@RequestParam("correo") String correo) {
        return listarTransaccionesDeUniCambista.ejecutar(correo);
    }
}
