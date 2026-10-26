package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.application.usecase.AsignarEncuentroSeguroUseCase;
import com.uniquindio.segundaMano.application.usecase.ConfirmarTransaccionUseCase;
import com.uniquindio.segundaMano.infrastructure.rest.dto.ConfirmacionBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.EncuentroSeguroBody;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.TransaccionRestMapper;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Operaciones que CAMBIAN cosas sobre las Transacciones (las consultas están en ConsultaTransaccionesController). */
@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final AsignarEncuentroSeguroUseCase asignarEncuentroSeguro;
    private final ConfirmarTransaccionUseCase confirmarTransaccion;
    private final TransaccionRestMapper mapper;

    public TransaccionController(AsignarEncuentroSeguroUseCase asignarEncuentroSeguro,
                                 ConfirmarTransaccionUseCase confirmarTransaccion,
                                 TransaccionRestMapper mapper) {
        this.asignarEncuentroSeguro = asignarEncuentroSeguro;
        this.confirmarTransaccion = confirmarTransaccion;
        this.mapper = mapper;
    }

    @PutMapping("/{id}/encuentro-seguro")
    public TransaccionResponse asignarEncuentroSeguro(@PathVariable("id") String id,
                                                      @Valid @RequestBody EncuentroSeguroBody body) {
        return asignarEncuentroSeguro.ejecutar(mapper.toRequest(id, body));
    }

    /** Endpoint semántico: se llama como el método del dominio (confirmar). */
    @PutMapping("/{id}/confirmar")
    public TransaccionResponse confirmar(@PathVariable("id") String id,
                                         @Valid @RequestBody ConfirmacionBody body) {
        return confirmarTransaccion.ejecutar(mapper.toRequest(id, body));
    }
}
