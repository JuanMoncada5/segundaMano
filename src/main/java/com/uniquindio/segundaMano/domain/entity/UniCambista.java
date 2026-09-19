package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.RachaConfianza;

import java.util.Objects;

public class UniCambista {

    private final String correoInstitucional;
    private String nombre;
    private boolean verificado;
    private RachaConfianza rachaConfianza;

    private UniCambista(String correoInstitucional, String nombre) {
        this.correoInstitucional = correoInstitucional;
        this.nombre = nombre;
        this.verificado = false;
        this.rachaConfianza = RachaConfianza.inicial();
    }

    public static UniCambista registrar(String correoInstitucional, String nombre) {
        if (correoInstitucional == null || !correoInstitucional.endsWith("@uniquindio.edu.co")) {
            throw new ReglaDominioException("El correo debe ser institucional");
        }
        return new UniCambista(correoInstitucional, nombre);
    }

    public void verificarCorreo() {
        this.verificado = true;
    }

    public boolean puedeCrearPublicacion() {
        return this.verificado;
    }

    public void actualizarReputacion(RachaConfianza nuevaRacha) {
        this.rachaConfianza = nuevaRacha;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public RachaConfianza getRachaConfianza() {
        return rachaConfianza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniCambista otro)) return false;
        return Objects.equals(correoInstitucional, otro.correoInstitucional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correoInstitucional);
    }
}