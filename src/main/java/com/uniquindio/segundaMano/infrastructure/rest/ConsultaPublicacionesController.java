package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.usecase.ConsultarPublicacionUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarPublicacionesActivasUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Solo consultas: no cambian nada. */
@RestController
@RequestMapping("/api/publicaciones")
public class ConsultaPublicacionesController {

    private final ListarPublicacionesActivasUseCase listarPublicacionesActivas;
    private final ConsultarPublicacionUseCase consultarPublicacion;

    public ConsultaPublicacionesController(ListarPublicacionesActivasUseCase listarPublicacionesActivas,
                                           ConsultarPublicacionUseCase consultarPublicacion) {
        this.listarPublicacionesActivas = listarPublicacionesActivas;
        this.consultarPublicacion = consultarPublicacion;
    }

    @GetMapping("/{codigo}")
    public PublicacionResponse consultar(@PathVariable("codigo") String codigo) {
        return consultarPublicacion.ejecutar(codigo);
    }

    @GetMapping
    public List<PublicacionResponse> listarActivas() {
        return listarPublicacionesActivas.ejecutar();
    }
}