package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import com.uniquindio.segundaMano.domain.valueObject.ZonaCampus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Value Object EncuentroSeguro guardado como dos columnas dentro de la tabla de la Transacción.
 * Si aún no se acordó el encuentro, las dos columnas quedan en null.
 */
@Embeddable
public class EncuentroSeguroEmbeddable {

    @Enumerated(EnumType.STRING)
    private ZonaCampus zona;

    private String referencia;

    /** JPA exige un constructor vacío. */
    protected EncuentroSeguroEmbeddable() {
    }

    public EncuentroSeguroEmbeddable(ZonaCampus zona, String referencia) {
        this.zona = zona;
        this.referencia = referencia;
    }

    public ZonaCampus getZona() {
        return zona;
    }

    public void setZona(ZonaCampus zona) {
        this.zona = zona;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
