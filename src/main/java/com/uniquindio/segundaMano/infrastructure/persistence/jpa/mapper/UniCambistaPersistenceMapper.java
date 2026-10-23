package com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper;

import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.valueObject.RachaConfianza;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.RachaConfianzaEmbeddable;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.UniCambistaEntity;
import org.springframework.stereotype.Component;

/** Traduce entre el UniCambista del dominio y su modelo JPA (el "bodeguero"). */
@Component
public class UniCambistaPersistenceMapper {

    public UniCambistaEntity toEntity(UniCambista uniCambista) {
        RachaConfianza racha = uniCambista.getRachaConfianza();
        return new UniCambistaEntity(
                uniCambista.getCorreoInstitucional(),
                uniCambista.getNombre(),
                uniCambista.isVerificado(),
                new RachaConfianzaEmbeddable(racha.promedioEstrellas(), racha.totalTransacciones())
        );
    }

    public UniCambista toDomain(UniCambistaEntity entity) {
        RachaConfianzaEmbeddable racha = entity.getRachaConfianza();
        return UniCambista.reconstruir(
                entity.getCorreoInstitucional(),
                entity.getNombre(),
                entity.isVerificado(),
                new RachaConfianza(racha.getPromedioEstrellas(), racha.getTotalTransacciones())
        );
    }
}
