package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.PublicacionRankingResponse;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Reporte: Publicaciones activas con más Ofertas. El JOIN + GROUP BY + COUNT lo hace la base de datos
 * (PublicacionJpaDataRepository); aquí solo se valida el límite y se arma la respuesta.
 */
public class ConsultarRankingPublicacionesUseCase {

    public static final int LIMITE_MAXIMO = 50;

    private final PublicacionRepository publicacionRepository;

    public ConsultarRankingPublicacionesUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public List<PublicacionRankingResponse> ejecutar(int limite) {
        if (limite < 1 || limite > LIMITE_MAXIMO) {
            throw new IllegalArgumentException("El límite debe estar entre 1 y " + LIMITE_MAXIMO);
        }
        List<Publicacion> masOfertadas = publicacionRepository.listarMasOfertadas(limite);
        List<PublicacionRankingResponse> ranking = new ArrayList<>();
        for (int i = 0; i < masOfertadas.size(); i++) {
            ranking.add(PublicacionRankingResponse.desde(i + 1, masOfertadas.get(i)));
        }
        return ranking;
    }
}