package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.PublicacionRankingResponse;
import com.uniquindio.segundaMano.application.usecase.ConsultarRankingPublicacionesUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Reportes para el administrador. Está bajo /api/admin/ así que SecurityConfig
 * solo deja entrar al rol ADMIN: es la segunda ruta restringida por rol.
 */
@RestController
@RequestMapping("/api/admin/reportes")
public class ReportesAdminController {

    private final ConsultarRankingPublicacionesUseCase consultarRanking;

    public ReportesAdminController(ConsultarRankingPublicacionesUseCase consultarRanking) {
        this.consultarRanking = consultarRanking;
    }

    /** Ejemplo: /api/admin/reportes/publicaciones-mas-ofertadas?limite=5 */
    @GetMapping("/publicaciones-mas-ofertadas")
    public List<PublicacionRankingResponse> publicacionesMasOfertadas(
            @RequestParam(name = "limite", defaultValue = "5") int limite) {
        return consultarRanking.ejecutar(limite);
    }
}