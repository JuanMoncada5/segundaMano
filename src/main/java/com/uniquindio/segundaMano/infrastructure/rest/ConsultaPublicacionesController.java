package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.BuscarPublicacionesRequest;
import com.uniquindio.segundaMano.application.dto.PaginaResponse;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.usecase.BuscarPublicacionesUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarPublicacionUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarPublicacionesActivasUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/** Solo consultas: no cambian nada. */
@RestController
@RequestMapping("/api/publicaciones")
public class ConsultaPublicacionesController {

    private final ListarPublicacionesActivasUseCase listarPublicacionesActivas;
    private final ConsultarPublicacionUseCase consultarPublicacion;
    private final BuscarPublicacionesUseCase buscarPublicaciones;

    public ConsultaPublicacionesController(ListarPublicacionesActivasUseCase listarPublicacionesActivas,
                                           ConsultarPublicacionUseCase consultarPublicacion,
                                           BuscarPublicacionesUseCase buscarPublicaciones) {
        this.listarPublicacionesActivas = listarPublicacionesActivas;
        this.consultarPublicacion = consultarPublicacion;
        this.buscarPublicaciones = buscarPublicaciones;
    }

    /**
     * Búsqueda paginada. Ejemplo: /api/publicaciones/buscar?texto=calc&valorMax=200000&pagina=0&tamano=10
     * Todos los filtros son opcionales; pagina empieza en 0.
     */
    @GetMapping("/buscar")
    public PaginaResponse<PublicacionResponse> buscar(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "curso", required = false) String curso,
            @RequestParam(name = "valorMin", required = false) BigDecimal valorMin,
            @RequestParam(name = "valorMax", required = false) BigDecimal valorMax,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamano", defaultValue = "10") int tamano) {
        return buscarPublicaciones.ejecutar(
                new BuscarPublicacionesRequest(texto, curso, valorMin, valorMax, pagina, tamano));
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