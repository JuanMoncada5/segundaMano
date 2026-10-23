package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Embeddable;

/**
 * Value Object RachaConfianza guardado como dos columnas dentro de la tabla del UniCambista.
 */
@Embeddable
public class RachaConfianzaEmbeddable {

    private double promedioEstrellas;

    private int totalTransacciones;

    /** JPA exige un constructor vacío. */
    protected RachaConfianzaEmbeddable() {
    }

    public RachaConfianzaEmbeddable(double promedioEstrellas, int totalTransacciones) {
        this.promedioEstrellas = promedioEstrellas;
        this.totalTransacciones = totalTransacciones;
    }

    public double getPromedioEstrellas() {
        return promedioEstrellas;
    }

    public void setPromedioEstrellas(double promedioEstrellas) {
        this.promedioEstrellas = promedioEstrellas;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    public void setTotalTransacciones(int totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }
}
