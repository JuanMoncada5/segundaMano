package com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Modelo JPA del UniCambista: solo datos, sin reglas de negocio (las reglas viven en el dominio).
 */
@Entity
@Table(name = "uni_cambistas")
public class UniCambistaEntity {

    @Id
    private String correoInstitucional;

    private String nombre;

    private boolean verificado;

    @Embedded
    private RachaConfianzaEmbeddable rachaConfianza;

    /** JPA exige un constructor vacío. */
    protected UniCambistaEntity() {
    }

    public UniCambistaEntity(String correoInstitucional, String nombre, boolean verificado, RachaConfianzaEmbeddable rachaConfianza) {
        this.correoInstitucional = correoInstitucional;
        this.nombre = nombre;
        this.verificado = verificado;
        this.rachaConfianza = rachaConfianza;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public RachaConfianzaEmbeddable getRachaConfianza() {
        return rachaConfianza;
    }

    public void setRachaConfianza(RachaConfianzaEmbeddable rachaConfianza) {
        this.rachaConfianza = rachaConfianza;
    }
}
